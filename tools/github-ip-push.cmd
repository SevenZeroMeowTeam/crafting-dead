@echo off
rem ---------------------------------------------------------------
rem  github-ip-push.cmd -- launcher for github-ip-push.ps1
rem
rem  Why this wrapper exists:
rem    1. PowerShell execution policy (RemoteSigned) may refuse to run
rem       an unsigned .ps1 directly.
rem    2. The .ps1 stores Chinese text as UTF-8 with BOM; launching it
rem       through powershell.exe keeps that encoding intact.
rem
rem  NOTE: keep this file ASCII-only. cmd.exe reads .cmd in the OEM code
rem  page (GBK here), so non-ASCII comments get mangled and can be parsed
rem  as commands, breaking the whole invocation.
rem
rem  Usage:
rem    github-ip-push.cmd                      scan + pin usable GitHub IPs only
rem    github-ip-push.cmd -Push                scan + push current branch
rem    github-ip-push.cmd -Push -Branch 1.20.x
rem    github-ip-push.cmd -TestOnly            scan only (no hosts write, no push)
rem    github-ip-push.cmd -Push -Repo "C:\path\to\repo"
rem ---------------------------------------------------------------
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0github-ip-push.ps1" %*
exit /b %ERRORLEVEL%
