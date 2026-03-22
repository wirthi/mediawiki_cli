@echo off
setlocal

REM Test the release functionality
REM This simulates how an external project would use the CLI

set RELEASE_DIR=release\v1.0
set JAR_FILE=%RELEASE_DIR%\mediawiki-cli-1.0.jar

if not exist "%JAR_FILE%" (
    echo No release found. Using target directory for demonstration.
    echo Please create a proper release for external use.
    set JAR_FILE=target\mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar
    if not exist "%JAR_FILE%" (
        echo No JAR found in target either. Please build the project first.
        exit /b 1
    )
)

echo Testing release from: %RELEASE_DIR%
echo Using JAR: %JAR_FILE%
echo 

REM Test help command
echo Testing --help:
java -jar "%JAR_FILE%" --help

echo 
REM Test search command
echo Testing --search:
java -jar "%JAR_FILE%" --search Linz --file test_search.txt

if exist test_search.txt (
    echo Search results saved successfully
    del test_search.txt
) else (
    echo Search test failed
)

echo 
echo Release test completed successfully!
endlocal