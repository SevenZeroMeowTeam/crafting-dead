$env:JAVA_HOME = "C:\Users\Administrator\.gradle\jdks\eclipse_adoptium-21-amd64-windows\jdk-21.0.12+8"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
Set-Location "C:\Users\Administrator\Desktop\crafting-dead-Kotlin"
& .\gradlew :crafting-dead-core:build --offline -x test
exit $LASTEXITCODE
