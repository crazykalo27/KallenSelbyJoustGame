# build & deploy Joust LibGDX game to GitHub Pages
# ------------------------------------------------
# USAGE  (from repo root, PowerShell):
#   ./scripts/deploy-web.ps1 "Optional commit message here"
#
# The script will:
#   1. Run the Gradle GWT/HTML build (creates html/build/dist/)
#   2. Replace the contents of docs/ with the fresh dist output
#   3. Ensure the .nojekyll marker is present (so GitHub Pages skips Jekyll)
#   4. Commit & push the changes to the current branch (default main)
#      – if you provide an argument, it is used as the commit message; otherwise a default is used.
#
# Prerequisites:  Git, Gradle wrapper, and PowerShell 5+.
#                 You must have push permissions on the current branch.

param(
    [string]$Message = "Update web deployment"
)

$ErrorActionPreference = 'Stop'

Write-Host "=== Step 1: Building GWT/HTML project via Gradle ===" -ForegroundColor Cyan
./gradlew :html:dist --no-daemon

Write-Host "=== Step 2: Refreshing docs/ folder ===" -ForegroundColor Cyan
if (Test-Path "docs") {
    Get-ChildItem -Path docs -Recurse -Force | Where-Object { $_.Name -ne '.nojekyll' } | Remove-Item -Recurse -Force
} else {
    New-Item -ItemType Directory -Path docs | Out-Null
}
Copy-Item -Recurse -Force "html/build/dist/*" docs

# Ensure .nojekyll exists so GitHub Pages serves JS/CSS as-is
if (-Not (Test-Path "docs/.nojekyll")) {
    New-Item -ItemType File -Path "docs/.nojekyll" | Out-Null
}

Write-Host "=== Step 3: Committing deployment artefacts ===" -ForegroundColor Cyan
& git add docs .nojekyll

$branch = (& git rev-parse --abbrev-ref HEAD).Trim()
if (-Not $branch) { $branch = 'main' }

& git commit -m $Message

Write-Host "=== Step 4: Pushing to origin/$branch ===" -ForegroundColor Cyan
& git push origin $branch

Write-Host "\n✅ Deployment pushed!  GitHub Actions will redeploy Pages automatically.\n" -ForegroundColor Green 