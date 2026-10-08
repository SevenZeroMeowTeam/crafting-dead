<#
.SYNOPSIS
  扫描可用的 GitHub IP，写入 hosts 的 hermes-github-pin 管理块，并（可选）执行 git push。

.DESCRIPTION
  背景：本网络下系统 DNS 有时把 github.com 解析到不可达的 IP（TLS 握手超时），
  表现为 git push 连接超时，或 GitHub 返回 Internal Server Error。

  做法：
    1. 从「已知地址段 + hosts 既有条目 + 系统 DNS + 公共 DNS」收集候选 IP；
    2. TCP 443 初筛（异步 TcpClient，短超时）；
    3. 用 curl --resolve 把域名钉到候选 IP 做真实 HTTPS 请求：
       - github.com 探的是仓库自己的 git 智能协议端点
         （/<owner>/<repo>/info/refs?service=git-upload-pack），
         那才是 push 真正走的地址，比探根路径准得多；
       - 只认 200 / 401；301/302 说明该 IP 并不服务这个域名
         （典型：拿 codeload 的 IP 去探 github.com），一律判不可用；
       - 耗时贴近 --max-time 的判为 slow，降权但仍留作备选；
    4. 按响应时间排序取前 N 个写入 hosts；
    5. 需要推送时逐个候选 IP 重试，且**每次重试都真的改写 hosts** ——
       否则 git 仍会解析到第一条，重试等于白试。

  只改动 hosts 中 >>> hermes-github-pin (managed) >>> 与 <<< hermes-github-pin <<<
  两行标记之间的内容；标记按「整行」匹配，不会误伤注释正文里引用到的标记文本。

.PARAMETER Repo
  仓库工作目录。默认取脚本所在目录的上一级。

.PARAMETER Push
  真正执行 git push；不带此开关时只扫描并固定 IP。

.PARAMETER Branch
  要推送的分支，默认取 Repo 当前分支。

.PARAMETER Remote
  远端名，默认 origin。

.PARAMETER DryRun
  推送时加 --dry-run（走完协商但不写入）。

.PARAMETER TestOnly
  只扫描并打印结果，不改 hosts、不推送。

.PARAMETER TopN
  写入 hosts 的 IP 个数（默认 3）。

.EXAMPLE
  .\github-ip-push.ps1 -TestOnly
.EXAMPLE
  .\github-ip-push.ps1 -Push
.EXAMPLE
  .\github-ip-push.ps1 -Push -Branch 1.20.x -Repo "C:\...\crafting-dead-1.20.x"
#>
[CmdletBinding()]
param(
  [string]$Repo,
  [switch]$Push,
  [string]$Branch,
  [string]$Remote = "origin",
  [switch]$DryRun,
  [switch]$TestOnly,
  [int]$TopN = 3,
  [int]$TcpTimeoutMs = 2500,
  [int]$CurlTimeoutSec = 12
)

$ErrorActionPreference = 'Continue'

$HostsFile = Join-Path $env:SystemRoot 'System32\drivers\etc\hosts'
$MarkBegin = '# >>> hermes-github-pin (managed) >>>'
$MarkEnd   = '# <<< hermes-github-pin <<<'

$KnownCandidates = [ordered]@{
  'github.com' = @(
    '140.82.112.3','140.82.112.4','140.82.113.3','140.82.113.4',
    '140.82.114.3','140.82.114.4','140.82.116.3','140.82.116.4',
    '140.82.121.3','140.82.121.4','20.205.243.166','20.27.177.113',
    '20.200.245.247','20.201.28.151','20.248.137.48','4.208.26.197','20.26.156.215'
  )
  'api.github.com' = @(
    '140.82.112.5','140.82.112.6','140.82.113.5','140.82.113.6',
    '140.82.114.5','140.82.114.6','20.205.243.168','20.27.177.116'
  )
  'codeload.github.com' = @(
    '140.82.112.9','140.82.112.10','140.82.113.9','140.82.113.10',
    '20.205.243.165','20.27.177.113'
  )
  'raw.githubusercontent.com' = @(
    '185.199.108.133','185.199.109.133','185.199.110.133','185.199.111.133',
    '20.27.177.113'
  )
}

# ------------------------------------------------------------------ helpers
function Get-HostsCandidates {
  $out = @{}
  if (-not (Test-Path $HostsFile)) { return $out }
  foreach ($l in [System.IO.File]::ReadAllLines($HostsFile)) {
    if ($l -match '^\s*#?\s*(\d{1,3}(?:\.\d{1,3}){3})\s+([A-Za-z0-9\.\-]+)') {
      $ip = $Matches[1]; $h = $Matches[2].ToLower()
      if (-not $out.ContainsKey($h)) { $out[$h] = New-Object System.Collections.Generic.List[string] }
      if (-not $out[$h].Contains($ip)) { [void]$out[$h].Add($ip) }
    }
  }
  return $out
}

function Resolve-WithPublicDns {
  param([string]$Name)
  $found = New-Object System.Collections.Generic.List[string]
  foreach ($s in @('223.5.5.5','119.29.29.29','1.1.1.1','8.8.8.8')) {
    try {
      $r = Resolve-DnsName -Name $Name -Server $s -Type A -QuickTimeout -ErrorAction Stop
      foreach ($a in $r) { if ($a.IPAddress -and -not $found.Contains($a.IPAddress)) { [void]$found.Add($a.IPAddress) } }
      if ($found.Count -gt 0) { break }
    } catch { }
  }
  return $found
}

function Test-TcpPort {
  param([string]$Ip, [int]$Port = 443, [int]$TimeoutMs = 2500)
  $client = New-Object System.Net.Sockets.TcpClient
  try {
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    $iar = $client.BeginConnect($Ip, $Port, $null, $null)
    if (-not $iar.AsyncWaitHandle.WaitOne($TimeoutMs, $false)) { return -1 }
    $client.EndConnect($iar)
    $sw.Stop()
    return [int]$sw.ElapsedMilliseconds
  } catch { return -1 }
  finally { try { $client.Close() } catch { } }
}

function Test-HttpsForHost {
  <# 用 curl --resolve 把域名钉到指定 IP 做真实 HTTPS 请求（校验证书）。
     只认 200 / 401；301/302 表示该 IP 并不服务这个域名。 #>
  param([string]$Domain, [string]$Ip, [int]$TimeoutSec = 12, [string]$Path = '/')
  $url = "https://$Domain$Path"
  $curl = (Get-Command curl.exe -ErrorAction SilentlyContinue).Source
  if (-not $curl) { return @{ Ok = $false; Code = 0; Ms = -1 } }
  $curlArgs = @(
    '--resolve', "${Domain}:443:${Ip}",
    '-sS', '-o', 'NUL',
    '-w', '%{http_code} %{time_total}',
    '--max-time', "$TimeoutSec",
    '--retry', '0',
    $url
  )
  $out = & $curl @curlArgs 2>$null
  if (-not $out) { return @{ Ok = $false; Code = 0; Ms = -1 } }
  $parts = ($out -split '\s+')
  if ($parts.Count -lt 2) { return @{ Ok = $false; Code = 0; Ms = -1 } }
  $code = [int]$parts[0]
  $ms = [int]([double]$parts[1] * 1000)
  $ok = ($code -eq 200) -or ($code -eq 401)
  return @{ Ok = $ok; Code = $code; Ms = $ms }
}

function Update-HostsBlock {
  <# 只替换管理块；块不存在则追加。
     标记按「整行」匹配 —— 用 IndexOf 找 '<<<' 会被注释正文里引用到的标记骗到，
     导致块范围算错，把 hosts 撕坏。 #>
  param([hashtable]$Chosen)

  $raw = if (Test-Path $HostsFile) { [System.IO.File]::ReadAllText($HostsFile) } else { "" }
  $nl = if ($raw -match "`r`n") { "`r`n" } else { "`n" }
  $stamp = (Get-Date).ToString('yyyy-MM-dd HH:mm:ss')

  $lines = New-Object System.Collections.Generic.List[string]
  [void]$lines.Add($MarkBegin)
  # 注释保持纯 ASCII：hosts 可能被按 OEM 代码页读取，非 ASCII 会显示成乱码。
  [void]$lines.Add("# Generated by tools/github-ip-push.ps1 at $stamp")
  [void]$lines.Add("# Usable GitHub IPs, sorted by HTTPS response time.")
  [void]$lines.Add("# Re-run that script when these stop working, or delete this block to fall back to DNS.")
  foreach ($d in $Chosen.Keys) {
    foreach ($ip in @($Chosen[$d])) { [void]$lines.Add("$ip $d") }
  }
  [void]$lines.Add($MarkEnd)
  $blockText = ($lines -join $nl)

  $beginRe = [regex]'(?m)^[ \t]*#[ \t]*>>>[ \t]*hermes-github-pin[ \t]*\(managed\)[ \t]*>>>[ \t]*$'
  $endRe   = [regex]'(?m)^[ \t]*#[ \t]*<<<[ \t]*hermes-github-pin[ \t]*<<<[ \t]*$'

  $mb = $beginRe.Match($raw)
  if ($mb.Success) {
    $me = $endRe.Match($raw, $mb.Index + $mb.Length)
    if ($me.Success) {
      $new = $raw.Substring(0, $mb.Index) + $blockText + $raw.Substring($me.Index + $me.Length)
    } else {
      $new = $raw.Substring(0, $mb.Index) + $blockText
    }
  } else {
    if ($raw.Length -gt 0 -and -not $raw.EndsWith($nl)) { $raw += $nl }
    $new = $raw + $nl + $blockText + $nl
  }

  $new = [regex]::Replace($new, "(`r?`n){3,}", "$nl$nl")
  if (-not $new.EndsWith($nl)) { $new += $nl }

  try {
    [System.IO.File]::WriteAllText($HostsFile, $new, (New-Object System.Text.UTF8Encoding($false)))
  } catch {
    Write-Host "  [!] 写入 hosts 失败：$($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "      请以管理员身份运行，或手工把选出的 IP 写进 hosts。" -ForegroundColor Yellow
    return $false
  }
  try { & ipconfig /flushdns | Out-Null } catch { }
  return $true
}

function Ensure-GitHttpsHelper {
  <# 本机 E:\Git 缺 git-remote-https.exe；缺了就临时从别的 Git 安装复制到 TEMP 并注入 PATH。 #>
  try {
    $exec = (& git --exec-path 2>&1 | Out-String).Trim()
    if ($exec -and (Test-Path (Join-Path $exec 'git-remote-https.exe'))) { return $true }
  } catch { }

  $tmp = Join-Path $env:TEMP 'git-https-helper'
  if (-not (Test-Path (Join-Path $tmp 'git-remote-https.exe'))) {
    $src = $null
    foreach ($root in @('C:\Program Files','C:\Program Files (x86)')) {
      if (-not (Test-Path $root)) { continue }
      $src = Get-ChildItem $root -Recurse -Filter 'git-remote-https.exe' -ErrorAction SilentlyContinue |
             Select-Object -First 1
      if ($src) { break }
    }
    if (-not $src) {
      Write-Host "  [!] 找不到 git-remote-https.exe，无法补全 HTTPS 支持" -ForegroundColor Yellow
      return $false
    }
    New-Item -ItemType Directory -Force -Path $tmp | Out-Null
    Copy-Item $src.FullName $tmp -Force
    $core = Split-Path $src.FullName -Parent
    $q = Join-Path $core 'git-remote-http.exe'
    if (Test-Path $q) { Copy-Item $q $tmp -Force }
    $binCandidates = @(
      (Join-Path (Split-Path (Split-Path $core -Parent) -Parent) 'bin'),
      (Join-Path (Split-Path $core -Parent) 'bin')
    )
    foreach ($b in $binCandidates) {
      if (Test-Path $b) { Copy-Item (Join-Path $b '*.dll') $tmp -Force -ErrorAction SilentlyContinue }
    }
    Write-Host "  [i] 已补齐 git HTTPS 助手到 $tmp" -ForegroundColor DarkGray
  }
  $env:PATH = "$tmp;$env:PATH"
  return $true
}

function Invoke-GitPush {
  param([string]$WorkDir, [string]$Rem, [string]$Br, [switch]$IsDryRun)
  $common = @(
    '-c','safe.directory=*',
    '-c','credential.helper=wincred',
    '-c','credential.modalprompt=false',
    '-c','http.postBuffer=524288000',
    '-c','http.lowSpeedLimit=0',
    '-c','http.version=HTTP/1.1'
  )
  $env:GIT_TERMINAL_PROMPT = '0'
  $argv = @('-C', $WorkDir) + $common + @('push')
  if ($IsDryRun) { $argv += '--dry-run' }
  if ($Br) { $argv += @($Rem, $Br) } else { $argv += $Rem }

  # 输出走文件重定向：git 把进度写到 stderr，直接 2>&1 在 PS 5.1 下会被包成
  # NativeCommandError 打到控制台，看起来像失败（其实不是）。
  $tmp = Join-Path $env:TEMP ("gitpush-" + [guid]::NewGuid().ToString('N') + ".log")
  $code = -1
  $out = ''
  try {
    # 交给 cmd.exe 执行：PowerShell 5.1 会把 native 程序的 stderr 包成
    # NativeCommandError 打到控制台（即使 2>&1 到文件也可能漏出来），
    # 看起来像推送失败，其实只是 git 的进度信息走了 stderr。
    $quoted = ($argv | ForEach-Object { if ($_ -match '[\s"]') { '"' + ($_ -replace '"','\"') + '"' } else { $_ } }) -join ' '
    & cmd.exe /c ("git " + $quoted + ' > "' + $tmp + '" 2>&1') | Out-Null
    $code = $LASTEXITCODE
    if (Test-Path $tmp) { $out = [System.IO.File]::ReadAllText($tmp) }
  } catch {
    $out = "push 调用异常: $($_.Exception.Message)"
  } finally {
    Remove-Item $tmp -Force -ErrorAction SilentlyContinue
  }
  if (-not $out) { $out = '' }
  $ok = ($code -eq 0) -or ($out -match 'up-to-date')
  return [pscustomobject]@{ Ok = $ok; Out = $out; Code = $code }
}

# ------------------------------------------------------------------ main
if (-not $Repo) { $Repo = Split-Path -Parent $PSScriptRoot }
if (-not (Test-Path $Repo)) { throw "Repo 不存在: $Repo" }

Write-Host "=== GitHub IP 扫描 ===" -ForegroundColor Cyan
Write-Host "仓库: $Repo"
Write-Host "hosts: $HostsFile"
Write-Host ""

# 1) 收集候选
$candidates = @{}
foreach ($d in $KnownCandidates.Keys) { $candidates[$d] = New-Object System.Collections.Generic.List[string] }
foreach ($d in $KnownCandidates.Keys) {
  foreach ($ip in $KnownCandidates[$d]) { if (-not $candidates[$d].Contains($ip)) { [void]$candidates[$d].Add($ip) } }
}
$fromHosts = Get-HostsCandidates
foreach ($h in $fromHosts.Keys) {
  if ($candidates.ContainsKey($h)) {
    foreach ($ip in $fromHosts[$h]) { if (-not $candidates[$h].Contains($ip)) { [void]$candidates[$h].Add($ip) } }
  }
}
foreach ($d in @($candidates.Keys)) {
  try {
    foreach ($a in [System.Net.Dns]::GetHostAddresses($d)) {
      $ip = $a.IPAddressToString
      if ($ip -match '^\d+\.' -and -not $candidates[$d].Contains($ip)) { [void]$candidates[$d].Add($ip) }
    }
  } catch { }
}
foreach ($d in @('github.com','api.github.com')) {
  foreach ($ip in (Resolve-WithPublicDns -Name $d)) {
    if ($candidates.ContainsKey($d) -and -not $candidates[$d].Contains($ip)) { [void]$candidates[$d].Add($ip) }
  }
}

# 2) TCP 443 初筛
$total = 0
foreach ($d in $candidates.Keys) { $total += $candidates[$d].Count }
Write-Host "候选总数: $total"
$alive = @{}
foreach ($d in $candidates.Keys) {
  $tcpOk = New-Object System.Collections.Generic.List[string]
  foreach ($ip in $candidates[$d]) {
    if ((Test-TcpPort -Ip $ip -Port 443 -TimeoutMs $TcpTimeoutMs) -ge 0) { [void]$tcpOk.Add($ip) }
  }
  $alive[$d] = $tcpOk
  Write-Host ("  {0,-28} TCP 443 可达 {1}/{2}" -f $d, $tcpOk.Count, $candidates[$d].Count)
}

# 3) HTTPS 精测
$probePath = '/'
try {
  $remoteUrl = (& git -C $Repo -c safe.directory='*' remote get-url $Remote 2>&1 | Out-String).Trim()
  if ($remoteUrl -match '^(?:https?://)?([^/]+)/(.+?)/?$') {
    $probePath = '/' + ($Matches[2] -replace '\.git$','') + '/info/refs?service=git-upload-pack'
  }
} catch { }

Write-Host ""
Write-Host "HTTPS 精测（curl --resolve，校验证书；只认 200/401）..." -ForegroundColor Cyan
Write-Host "  github.com 探针: $probePath" -ForegroundColor DarkGray

$chosen = [ordered]@{}
foreach ($d in $alive.Keys) {
  $fast = @(); $slow = @()
  $path = if ($d -eq 'github.com') { $probePath } else { '/' }
  foreach ($ip in $alive[$d]) {
    $r = Test-HttpsForHost -Domain $d -Ip $ip -TimeoutSec $CurlTimeoutSec -Path $path
    if ($r.Ok) {
      $isSlow = ($r.Ms -ge ($CurlTimeoutSec * 900))
      $rec = [pscustomobject]@{ Ip = $ip; Ms = $r.Ms }
      if ($isSlow) { $slow += $rec } else { $fast += $rec }
      Write-Host ("    {0} {1,-22} {2,5} ms  HTTP {3}" -f $(if ($isSlow) {'slow'} else {'ok  '}), $ip, $r.Ms, $r.Code) `
        -ForegroundColor $(if ($isSlow) {'Yellow'} else {'Green'})
    } else {
      Write-Host ("    fail {0,-22} HTTP {1}" -f $ip, $r.Code) -ForegroundColor DarkGray
    }
  }
  $ordered = @($fast | Sort-Object Ms) + @($slow | Sort-Object Ms)
  $chosen[$d] = if ($ordered.Count -gt 0) { @($ordered | Select-Object -First $TopN | ForEach-Object { $_.Ip }) } else { @() }
}

if (@($chosen['github.com']).Count -eq 0) {
  Write-Host ""
  Write-Host "没有找到可用的 github.com IP，未改动 hosts。" -ForegroundColor Red
  exit 2
}

# 4) 写 hosts
if (-not $TestOnly) {
  Write-Host ""
  Write-Host "写入 hosts 管理块：" -ForegroundColor Cyan
  foreach ($d in $chosen.Keys) {
    if (@($chosen[$d]).Count -gt 0) { Write-Host ("  {0,-28} {1}" -f $d, (@($chosen[$d]) -join ', ')) }
  }
  if (Update-HostsBlock -Chosen $chosen) { Write-Host "  hosts 已更新，DNS 缓存已刷新。" }
} else {
  Write-Host ""
  Write-Host "(-TestOnly：未写入 hosts)" -ForegroundColor Yellow
}

# 5) 推送
if ($Push -and -not $TestOnly) {
  Write-Host ""
  Write-Host "=== 推送 ===" -ForegroundColor Cyan
  Ensure-GitHttpsHelper | Out-Null
  $br = if ($Branch) { $Branch } else { (& git -C $Repo -c safe.directory='*' rev-parse --abbrev-ref HEAD 2>&1 | Out-String).Trim() }
  Write-Host "分支: $br -> $Remote"

  $tried = @()
  foreach ($ip in @($chosen['github.com'])) {
    $tried += $ip
    # 关键：每次重试都真的把 hosts 指到当前候选 IP，否则重试等于白试
    if (-not $DryRun) {
      $only = [ordered]@{}
      foreach ($d in $chosen.Keys) { $only[$d] = if ($d -eq 'github.com') { @($ip) } else { @($chosen[$d]) } }
      Update-HostsBlock -Chosen $only | Out-Null
    }
    Write-Host "  尝试解析到 $ip ..." -NoNewline
    $r = Invoke-GitPush -WorkDir $Repo -Rem $Remote -Br $br -IsDryRun:$DryRun
    if ($r.Ok) {
      Write-Host " 成功" -ForegroundColor Green
      if ($r.Out) { Write-Host $r.Out.Trim() }
      # 成功也写回完整候选列表：成功 IP 排最前，其余留作备用
      if (-not $DryRun) {
        $keep = [ordered]@{}
        foreach ($d in $chosen.Keys) {
          $lst = New-Object System.Collections.Generic.List[string]
          if ($d -eq 'github.com') { [void]$lst.Add($ip) }
          foreach ($x in @($chosen[$d])) { if ($x -ne $ip -and -not $lst.Contains($x)) { [void]$lst.Add($x) } }
          $keep[$d] = @($lst)
        }
        Update-HostsBlock -Chosen $keep | Out-Null
      }
      Write-Host ""
      Write-Host "推送完成（使用 IP: $ip）。" -ForegroundColor Green
      exit 0
    } else {
      Write-Host " 失败" -ForegroundColor Yellow
      $tail = (($r.Out -split "`r?`n") | Where-Object { $_ -match '\S' } | Select-Object -Last 2) -join ' | '
      if ($tail) { Write-Host "    $tail" -ForegroundColor DarkGray }
    }
  }

  if (-not $DryRun) { Update-HostsBlock -Chosen $chosen | Out-Null }
  Write-Host ""
  Write-Host "所有候选 IP 均推送失败（已尝试: $($tried -join ', ')）。" -ForegroundColor Red
  Write-Host "建议：稍后重试，或用 -TestOnly 重新扫描。" -ForegroundColor Yellow
  exit 3
}

Write-Host ""
Write-Host "完成。" -ForegroundColor Green
if (-not $Push) { Write-Host "（未推送；加 -Push 才会执行 git push）" -ForegroundColor DarkGray }
exit 0
