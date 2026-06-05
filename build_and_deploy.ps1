$modId = "sayuki"
$targetDir = "$env:USERPROFILE\Desktop"
$sourceDir = "$PSScriptRoot\build\libs"

Write-Host "=== Build $modId ===" -ForegroundColor Cyan

Push-Location $PSScriptRoot
try {
    .\gradlew.bat build --no-daemon 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "BUILD FAILED" -ForegroundColor Red
        exit 1
    }
    Write-Host "BUILD SUCCESS" -ForegroundColor Green

    if (-not (Test-Path $targetDir)) {
        New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
    }

    Get-ChildItem -Path $targetDir -Filter "$modId-*.jar" | Remove-Item -Force -ErrorAction SilentlyContinue

    $newJar = Get-ChildItem -Path $sourceDir -Filter "$modId-*.jar" | Select-Object -First 1
    if (-not $newJar) {
        Write-Host "JAR not found in $sourceDir" -ForegroundColor Red
        exit 1
    }

    Copy-Item -Path $newJar.FullName -Destination $targetDir -Force
    Write-Host "Copied: $($newJar.Name) -> $targetDir" -ForegroundColor Green

} finally {
    Pop-Location
}

Write-Host "=== Done ===" -ForegroundColor Cyan
