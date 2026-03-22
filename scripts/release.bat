@echo off
setlocal enabledelayedexpansion

REM MediaWiki CLI Release Script
REM Usage: release.bat <version>

if "%1"=="" (
    echo Usage: %0 ^<version^>
    echo Example: %0 1.0
    exit /b 1
)

set VERSION=%1
set RELEASE_DIR=release\v%VERSION%

REM Build the project
echo Building project...
mvn clean package
if %ERRORLEVEL% neq 0 (
    echo Build failed
    exit /b %ERRORLEVEL%
)

REM Create release directory
echo Creating release directory %RELEASE_DIR%...
mkdir "%RELEASE_DIR%"
if %ERRORLEVEL% neq 0 (
    echo Failed to create release directory
    exit /b %ERRORLEVEL%
)

REM Copy artifacts
echo Copying artifacts...
copy target\mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar "%RELEASE_DIR%\mediawiki-cli-%VERSION%.jar"
copy README.md "%RELEASE_DIR%\"
copy CREDENTIALS.template.txt "%RELEASE_DIR%\"

REM Create latest symlink (Windows uses junction instead)
echo Creating latest symlink...
rmdir /q release\latest 2>nul
mklink /J release\latest "%RELEASE_DIR%"

REM Create version info
echo Creating version info...
(
    echo MediaWiki CLI %VERSION%
    echo Released: %DATE% %TIME%
    echo 
    echo Usage:
    echo   java -jar mediawiki-cli-%VERSION%.jar --help
) > "%RELEASE_DIR%\VERSION.txt"

echo Release %VERSION% created successfully in %RELEASE_DIR%
echo External projects should use: release\latest\mediawiki-cli-%VERSION%.jar
endlocal