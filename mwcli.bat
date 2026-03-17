@echo off
setlocal

:: Check if JAR file exists
if not exist "target\mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar" (
    echo Building the project...
    call mvn clean package
    if errorlevel 1 (
        echo Build failed
        exit /b 1
    )
)

:: Check if credentials file exists
if not exist "CREDENTIALS.txt" (
    echo Creating default CREDENTIALS.txt file...
    echo site=https://www.linzwiki.at/ > CREDENTIALS.txt
)

:: Run the Java command with all arguments
java -jar target\mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar %*

endlocal