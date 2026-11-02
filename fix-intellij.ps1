# Javitja az IntelliJ-ben beragadt, hibas Maven importot a ket zh-vaadin projektnel.
# FUTTATAS ELOTT ZARD BE AZ INTELLIJ-T!
#
# Mit csinal:
#  1. Torli az IntelliJ per-projekt cache-et (kulso modul-tarolo), amiben a
#     "LANGUAGE_LEVEL=JDK_1_5" + nulla Maven fuggoseg beragadt -> ebbol jon a
#     "Cannot compile module ... configured for JVM target 5" hiba.
#  2. Torli a masolt workspace.xml-t a sajat megoldasbol, mert abban ugyanaz a
#     ProjectId szerepel, mint az eredeti MintaZH projektben (a .idea masolasa miatt),
#     es ettol az IntelliJ osszekeverheti a ket projektet.
#
# Minden torolt fajl regeneralodik a projekt kovetkezo megnyitasakor.

$ErrorActionPreference = 'Stop'

$idea = Get-Process idea64 -ErrorAction SilentlyContinue
if ($idea) {
    Write-Host "HIBA: az IntelliJ meg fut (PID $($idea.Id)). Zard be, aztan futtasd ujra." -ForegroundColor Red
    exit 1
}

$cacheRoot = "$env:LOCALAPPDATA\JetBrains\IntelliJIdea2025.3\projects"

# 1) beragadt per-projekt cache-ek
foreach ($name in @('zh-vaadin', 'zh1_vaadin_by_claude')) {
    Get-ChildItem -Path $cacheRoot -Directory -Filter "$name.*" -ErrorAction SilentlyContinue | ForEach-Object {
        Write-Host "Torlom a cache-t: $($_.FullName)"
        Remove-Item -Recurse -Force $_.FullName
    }
}

# 2) masolt workspace.xml (duplikalt ProjectId) + atmasolt PHP-beallitas
$copyIdea = "$PSScriptRoot\zh1_vaadin_by_claude\.idea"
foreach ($f in @('workspace.xml', 'php.xml')) {
    $path = Join-Path $copyIdea $f
    if (Test-Path $path) {
        Write-Host "Torlom: $path"
        Remove-Item -Force $path
    }
}

Write-Host ""
Write-Host "Kesz. Most nyisd meg az IntelliJ-t, es a POM.XML fajlra jobb klikk ->" -ForegroundColor Green
Write-Host "'Add as Maven Project' (vagy Maven panel -> Reload All Maven Projects)." -ForegroundColor Green
