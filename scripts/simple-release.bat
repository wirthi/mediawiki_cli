@echo off
setlocal

REM Simple Release Script - copies existing JAR to release directory
REM Usage: simple-release.bat <version>

if "%1"=="" (
    echo Usage: %0 ^<version^>
    echo Example: %0 1.0
    exit /b 1
)

set VERSION=%1
set RELEASE_DIR=release\v%VERSION%

REM Check if JAR exists
if not exist "target\mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar" (
    echo No JAR found. Please build the project first with 'mvn package'
    exit /b 1
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
copy mwcli.bat "%RELEASE_DIR%\"

REM Create version info
echo Creating version info...
(
    echo MediaWiki CLI %VERSION%
    echo Released: %DATE% %TIME%
    echo 
    echo Built from current development state
    echo 
    echo Usage:
    echo   java -jar mediawiki-cli-%VERSION%.jar --help
) > "%RELEASE_DIR%\VERSION.txt"

REM Create/update latest directory
REM Instead of symlink, we'll copy to latest for simplicity
robocopy "%RELEASE_DIR%" "release\latest" /MIR

if %ERRORLEVEL% leq 1 (
    echo Release %VERSION% created successfully in %RELEASE_DIR%
    echo External projects should use: release\latest\mediawiki-cli-%VERSION%.jar
) else (
    echo Error copying to latest directory
)

endlocal