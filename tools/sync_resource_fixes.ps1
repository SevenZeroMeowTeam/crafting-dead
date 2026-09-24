#requires -Version 5.1
<#
.SYNOPSIS
  把 Crafting Dead Kotlin 1.20.x 分支上修好的一批资源/配置问题同步到其它分支工作树。

.DESCRIPTION
  修复项（每项都先检测再动手，已修好的分支自动跳过）：
   1. 新增 assets/minecraft/atlases/blocks.json —— 1.19.3+ 起不再自动拼接“模型引用的贴图”，
      hats/ backpack/ vest/ mythic/ 四个目录必须在图集里显式声明，否则日志刷 87 条 Missing textures。
   2. 装饰模组 3 处写错的贴图路径（lab_mixer / levator_buttons_01 / traffic_signs_stop_radioactive）。
   3. 资源文件名非法（大写、空格）—— MC 直接忽略并报 "Invalid path in pack"。
   4. 多一个大括号 / 尾部残留 groups 的坏 JSON（Gson 宽容才没报错）。
   5. ServerConfig 中越出声明范围的默认值 criticalHit.bonusDamage = 0.5D, 范围 [1, 10]。

  只写工作区文件、不提交 git；回滚：git -C <dir> checkout -- .

.EXAMPLE
  ./sync_resource_fixes.ps1 -Target C:\Users\Administrator\Desktop\crafting-dead-1.20.x -DryRun
  ./sync_resource_fixes.ps1 -Target C:\Users\Administrator\Desktop\crafting-dead-1.20.x, C:\Users\Administrator\Desktop\crafting-dead-1.18.x
#>
param(
  [Parameter(Mandatory = $true)][string[]]$Target,
  [switch]$DryRun
)

$ErrorActionPreference = 'Stop'
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)
$script:applied = 0; $script:skipped = 0; $script:failed = 0

function Log([string]$tag, [string]$msg) {
  Write-Output ("  [{0,-4}] {1}" -f $tag, $msg)
  switch ($tag) { 'ok' { $script:applied++ } 'skip' { $script:skipped++ } 'ERR' { $script:failed++ } }
}

function Read-Text([string]$path) { return [IO.File]::ReadAllText($path) }

function Write-Text([string]$path, [string]$text) {
  if ($DryRun) { return }
  $dir = Split-Path -Parent $path
  if (-not (Test-Path -LiteralPath $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
  [IO.File]::WriteAllText($path, $text, $Utf8NoBom)
}

function Get-BraceDepth([string]$text) {
  $depth = 0; $inStr = $false; $esc = $false
  foreach ($ch in $text.ToCharArray()) {
    if ($inStr) { if ($esc) { $esc = $false } elseif ($ch -eq '\') { $esc = $true } elseif ($ch -eq '"') { $inStr = $false }; continue }
    if ($ch -eq '"') { $inStr = $true }
    elseif ($ch -eq '{') { $depth++ }
    elseif ($ch -eq '}') { $depth-- }
  }
  return $depth
}

function Replace-Literal([string]$path, [string]$old, [string]$new, [string]$label) {
  if (-not (Test-Path -LiteralPath $path)) { Log 'skip' "$label（文件不存在）"; return }
  $t = Read-Text $path
  if (-not $t.Contains($old)) { Log 'skip' "$label（无需修改）"; return }
  $n = ([regex]::Matches($t, [regex]::Escape($old))).Count
  Write-Text $path $t.Replace($old, $new)
  Log 'ok' "$label（$n 处）"
}

function Get-RealName([string]$path) {
  # 返回磁盘上真实文件名（区分大小写）。Windows 下 Test-Path 对大小写不敏感，
  # 必须枚举目录才能拿到真实拼写，否则会把同一文件误判成“重复文件”删掉。
  $dir = Split-Path -Parent $path
  if (-not (Test-Path -LiteralPath $dir)) { return $null }
  $item = Get-ChildItem -LiteralPath $dir -File -ErrorAction SilentlyContinue | Where-Object { $_.Name -ieq (Split-Path -Leaf $path) } | Select-Object -First 1
  if ($item) { return $item.Name }
  return $null
}

function Fix-InvalidPathFile([string]$badPath, [string]$goodPath) {
  $dir = Split-Path -Parent $badPath
  $real = Get-RealName $badPath
  if (-not $real) { return }                 # 该名字在磁盘上不存在
  $wantName = Split-Path -Leaf $goodPath
  $realGood = Get-RealName $goodPath
  $realPath = Join-Path $dir $real

  if ($realGood -and ($real -ceq $realGood)) {
    # 两种拼写指向同一个文件（仅大小写不同）：只能改大小写，绝不能删除
    if ($real -ceq $wantName) { Log 'skip' "$real（文件名已合法）"; return }
    if (-not $DryRun) {
      $tmp = $realPath + '.casetmp'
      Move-Item -LiteralPath $realPath -Destination $tmp
      Move-Item -LiteralPath $tmp -Destination $goodPath
    }
    Log 'ok' "$real -> $wantName（仅大小写改名）"
    return
  }
  if ($realGood) {
    $same = (Get-FileHash -LiteralPath $realPath -Algorithm SHA256).Hash -eq (Get-FileHash -LiteralPath (Join-Path $dir $realGood) -Algorithm SHA256).Hash
    if ($same) {
      if (-not $DryRun) { Remove-Item -LiteralPath $realPath -Force }
      Log 'ok' "删除重复文件 $real（与合法文件 $realGood 内容一致）"
    } else {
      $alt = Join-Path $dir ([IO.Path]::GetFileNameWithoutExtension($goodPath) + '_dup.png')
      if (-not $DryRun) { Move-Item -LiteralPath $realPath -Destination $alt }
      Log 'ok' "$real -> $(Split-Path -Leaf $alt)（合法名已被占用且内容不同）"
    }
  } else {
    if (-not $DryRun) { Move-Item -LiteralPath $realPath -Destination $goodPath }
    Log 'ok' "$real -> $wantName"
  }
}

function Repair-Json([string]$path, [string]$relPath) {
  $t = Read-Text $path
  if ((Get-BraceDepth $t) -eq 0) { return }
  $merged = $false
  # 情况 A：Blockbench 尾部残留 groups 片段（对象已闭合后又跟了一段 "groups": [...] 和一个多余大括号）
  $rx = [regex]'(?m)^([ \t]*)\][ \t]*\r?\n\}[ \t]*\r?\n[ \t]*"groups"[ \t]*:[ \t]*\['
  if ($rx.IsMatch($t)) {
    $t = $rx.Replace($t, ('$1],' + "`n" + '"groups": ['), 1)
    $merged = $true
  }
  # 情况 B：末尾多一个右大括号
  $trim = $t.TrimEnd()
  if ($trim.EndsWith('}')) {
    $cand = $trim.Substring(0, $trim.Length - 1)
    if ((Get-BraceDepth $cand) -eq 0) { $t = $cand + "`n" }
  }
  if ((Get-BraceDepth $t) -ne 0) { Log 'ERR' "$relPath 大括号仍不平衡，跳过"; return }
  $ok = $false
  try { $null = $t | ConvertFrom-Json; $ok = $true } catch { $ok = $false }
  if (-not $ok) {
    Log 'ERR' "$relPath 修完仍无法解析为 JSON，跳过（请人工处理）"
    return
  }
  Write-Text $path $t
  $how = '（去掉多余的尾部大括号）'
  if ($merged) { $how = '（合并尾部 groups、去掉多余大括号）' }
  Log 'ok' "$relPath $how"
}

foreach ($root in $Target) {
  Write-Output "==================== $root ===================="
  if (-not (Test-Path -LiteralPath $root)) { Log 'ERR' '目标目录不存在'; continue }
  $gitfile = Get-Content -LiteralPath "$root\.git" -ErrorAction SilentlyContinue
  if ($gitfile -match 'gitdir:\s*(.+)') { $head = (Get-Content -LiteralPath ($Matches[1].Trim() + '\HEAD') -ErrorAction SilentlyContinue) }
  else { $head = (Get-Content -LiteralPath "$root\.git\HEAD" -ErrorAction SilentlyContinue) }
  $mc = (Select-String -LiteralPath "$root\gradle.properties" -Pattern 'minecraft_version' -ErrorAction SilentlyContinue | Select-Object -First 1).Line
  Log 'plan' "$head | $($mc -replace '\s', '')"

  $coreRes = "$root\crafting-dead-core\src\main\resources"
  $survRes = "$root\crafting-dead-survival\src\main\resources"
  $decoRes = "$root\crafting-dead-decoration\src\main\resources"
  $decoAssets = "$decoRes\assets\craftingdeaddecoration"

  # ---------- 1. 图集 ----------
  $texRoot = "$coreRes\assets\craftingdead\textures"
  $atlas = "$coreRes\assets\minecraft\atlases\blocks.json"
  if (Test-Path -LiteralPath $atlas) {
    Log 'skip' 'blocks.json 图集已存在'
  } elseif (-not (Test-Path -LiteralPath $texRoot)) {
    Log 'ERR' "找不到贴图目录 $texRoot"
  } else {
    $entries = New-Object System.Collections.Generic.List[string]
    foreach ($d in @('hats', 'backpack', 'vest', 'mythic')) {
      $dirPath = Join-Path $texRoot $d
      if (-not (Test-Path -LiteralPath $dirPath)) { Log 'skip' "textures/$d 不存在"; continue }
      foreach ($f in (Get-ChildItem -LiteralPath $dirPath -Recurse -File -Filter *.png | Sort-Object FullName)) {
        $rel = $f.FullName.Substring($texRoot.Length + 1).Replace('\', '/') -replace '\.png$', ''
        $entries.Add("    { `"type`": `"single`", `"resource`": `"craftingdead:$rel`" }")
      }
    }
    if ($entries.Count -eq 0) { Log 'ERR' '未收集到贴图，跳过' }
    else {
      Write-Text $atlas ("{`n  `"sources`": [`n" + ($entries -join ",`n") + "`n  ]`n}`n")
      Log 'ok' "新增 blocks.json 图集（$($entries.Count) 条 single 源）"
    }
  }

  # ---------- 2. 装饰模组贴图路径 ----------
  Replace-Literal "$decoAssets\models\block\lab_mixer.json" '"craftingdeaddecoration:lab_mixer"' '"craftingdeaddecoration:block/lab_mixer"' 'lab_mixer 贴图路径'
  Replace-Literal "$decoAssets\models\block\elevator_buttons_01.json" 'block/levator_buttons_01' 'block/elevator_buttons_01' 'elevator_buttons_01 粒子贴图拼写'
  Replace-Literal "$decoAssets\models\item\l_traffic_signs_radioactive.json" 'block/traffic_signs_stop_radioactive' 'block/traffic_signs_radioactive' 'l_traffic_signs_radioactive 贴图路径'

  # ---------- 3. 非法文件名 ----------
  Fix-InvalidPathFile "$coreRes\assets\craftingdead\textures\attachment\iron_sight .png" "$coreRes\assets\craftingdead\textures\attachment\iron_sight.png"
  $decoBlock = "$decoAssets\textures\block"
  Fix-InvalidPathFile "$decoBlock\brown_Industrial_tiles.png" "$decoBlock\brown_industrial_tiles.png"
  Fix-InvalidPathFile "$decoBlock\brown_Industrial_tiles_crack.png" "$decoBlock\brown_industrial_tiles_crack.png"
  Fix-InvalidPathFile "$decoBlock\skeleton_mossy 1.png" "$decoBlock\skeleton_mossy_1.png"
  Fix-InvalidPathFile "$decoBlock\traffic signs pipe.png" "$decoBlock\traffic_signs_pipe.png"
  Fix-InvalidPathFile "$decoBlock\foliage\wood1_n_MAT.png" "$decoBlock\foliage\wood1_n_mat.png"

  # ---------- 4. 坏 JSON ----------
  foreach ($res in @($coreRes, $survRes, $decoRes)) {
    if (-not (Test-Path -LiteralPath $res)) { continue }
    foreach ($f in (Get-ChildItem -LiteralPath $res -Recurse -File -Filter *.json | Where-Object { $_.FullName -notmatch '\\src\\generated\\' })) {
      Repair-Json $f.FullName $f.FullName.Substring($root.Length + 1)
    }
  }

  # ---------- 5. 越界默认值 ----------
  Replace-Literal "$root\crafting-dead-core\src\main\java\com\craftingdead\core\ServerConfig.java" '"bonusDamage", 0.5D, 1D, 10D' '"bonusDamage", 1.0D, 1D, 10D' 'criticalHit.bonusDamage 默认值 -> 1.0D'

  # ---------- 校验 ----------
  $badJson = @(); $badPath = @()
  foreach ($res in @($coreRes, $survRes, $decoRes)) {
    if (-not (Test-Path -LiteralPath $res)) { continue }
    foreach ($f in (Get-ChildItem -LiteralPath $res -Recurse -File -Filter *.json | Where-Object { $_.FullName -notmatch '\\src\\generated\\' })) {
      if ((Get-BraceDepth (Read-Text $f.FullName)) -ne 0) { $badJson += $f.FullName.Substring($root.Length + 1) }
    }
  }
  Get-ChildItem -LiteralPath $root -Recurse -File -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -match '\\src\\main\\resources\\' } |
    ForEach-Object { $_.FullName.Substring($root.Length + 1) } |
    Where-Object { $_ -cnotmatch '^[a-z0-9_\\\-\./]+$' -and $_ -notmatch '\\META-INF\\' -and $_ -notmatch '^(HEADER|LICENSE)' } |
    ForEach-Object { $badPath += $_ }
  $atlasCount = '缺'
  if (Test-Path -LiteralPath $atlas) { $atlasCount = (Select-String -LiteralPath $atlas -Pattern '"type": "single"' -AllMatches | ForEach-Object { $_.Matches.Count } | Measure-Object -Sum).Sum }
  $s1 = ''; if ($badJson.Count) { $s1 = ' ' + ($badJson -join ',') }
  $s2 = ''; if ($badPath.Count) { $s2 = ' ' + ($badPath -join ',') }
  Log 'plan' "校验: JSON 不平衡=$($badJson.Count)$s1 | 非法路径=$($badPath.Count)$s2 | atlas=$atlasCount"
}

$mode = ''
if ($DryRun) { $mode = '(DryRun，未写入磁盘)' }
Write-Output ("=== 汇总: 已改 $($script:applied) / 跳过 $($script:skipped) / 失败 $($script:failed) $mode ===")
