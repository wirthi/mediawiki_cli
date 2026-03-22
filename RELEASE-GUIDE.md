# MediaWiki CLI Release Guide

This guide explains how to create and use stable releases of the MediaWiki CLI tool.

## Problem

When developing the MediaWiki CLI tool, you may want to:
1. Continue development in this directory
2. Use the CLI functionality from other projects
3. Ensure external projects don't break when you rebuild

## Solution: Release Directory

Use the `release/` directory to maintain stable versions separate from development.

## Directory Structure

```
mediawiki_cli/
├── src/                  # Development source code
├── target/               # Maven build output (development)
├── release/              # Stable releases for external use
│   ├── v1.0/             # Versioned releases
│   │   ├── mediawiki-cli-1.0.jar  # Stable JAR file
│   │   ├── mwcli.bat              # Convenience batch file
│   │   ├── CREDENTIALS.template.txt # Credentials template
│   │   ├── VERSION.txt            # Version information
│   │   └── README.txt            # Release notes
│   └── latest/           # Symlink/junction to current stable
├── scripts/              # Release management scripts
└── README.md
```

## Creating a Release

### Current Status

The project has a working release structure with version 1.0 already created. The release includes:
- Stable JAR file with all features
- Convenience batch file (`mwcli.bat`)
- Credentials template
- Documentation

### Step 1: Build and test your changes

```bash
mvn clean test
```

Make sure all tests pass before creating a release.

### Step 2: Create a release (Manual Method)

Since the automated script has some issues, here's how to create a release manually:

```batch
REM After successful build and testing:
mkdir release\v1.1
copy target\mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar "release\v1.1\mediawiki-cli-1.1.jar"
copy README.md "release\v1.1\"
copy CREDENTIALS.template.txt "release\v1.1\"
copy mwcli.bat "release\v1.1\"

REM Create VERSION.txt
echo MediaWiki CLI 1.1 > release\v1.1\VERSION.txt
echo Released: %DATE% >> release\v1.1\VERSION.txt

REM Update latest directory
robocopy release\v1.1 release\latest /MIR
```

### Step 3: Test the release

```batch
scripts\test-release.bat
```

This simulates how external projects will use the CLI.

## Using a Release in External Projects

### Option A: Use the batch file (Recommended)

The easiest way is to use the provided `mwcli.bat` batch file:

```batch
REM Copy the release directory to your project
xcopy "C:\path\to\mediawiki_cli\release\v1.0" "C:\your\project\cli" /E

REM Navigate to your project directory
cd C:\your\project\cli

REM Use the batch file (creates credentials automatically if needed)
mwcli.bat --search "Linz"
mwcli.bat --read "Hauptseite" --file output.txt
mwcli.bat --read-category "Linz" --file category.txt
mwcli.bat --update "TestPage" --content "New content" --summary "Updated via CLI"
```

**Benefits of using mwcli.bat:**
- Automatically finds the JAR file
- Creates default credentials if missing
- Provides helpful error messages
- Works from any directory

### Option B: Direct JAR usage

```batch
REM Reference the JAR directly
set CLI_JAR=C:\path\to\mediawiki_cli\release\v1.0\mediawiki-cli-1.0.jar

REM Make sure credentials exist in current directory
if not exist "CREDENTIALS.txt" (
    copy "C:\path\to\mediawiki_cli\release\v1.0\CREDENTIALS.template.txt" .
)

java -jar "%CLI_JAR%" --help
```

### Option C: Copy to external project

```batch
REM Copy the entire release to your project
xcopy "C:\path\to\mediawiki_cli\release\v1.0" "C:\external\project\libs\cli" /E

REM Use from the new location
cd C:\external\project\libs\cli
mwcli.bat --search "test"
```

## How the Batch File Works

The `mwcli.bat` file in each release provides several key features:

### 1. **Self-contained Execution**
```batch
set "SCRIPT_DIR=%~dp0"
set "JAR_FILE=%SCRIPT_DIR%\mediawiki-cli-1.0.jar"
```
- Automatically finds the JAR file relative to the batch file location
- Works even if you copy the release directory elsewhere

### 2. **Automatic Credentials Creation**
```batch
if not exist "CREDENTIALS.txt" (
    echo Creating default CREDENTIALS.txt file...
    (
        echo site=https://www.linzwiki.at/
        echo user=
        echo password=
        echo.
        echo Important: Edit this file with your actual credentials
    ) > CREDENTIALS.txt
)
```
- Creates a template credentials file if none exists
- Includes helpful comments for users
- Places file in the current working directory

### 3. **Error Handling**
```batch
if not exist "%JAR_FILE%" (
    echo Error: JAR file not found: %JAR_FILE%
    exit /b 1
)
```
- Clear error messages if files are missing
- Prevents cryptic Java error messages

## Credentials Management

### Where credentials are read from

The CLI reads credentials from the **current working directory**:

```
YourProject/
├── mwcli.bat          # (copied from release)
├── mediawiki-cli-1.0.jar  # (copied from release)
├── CREDENTIALS.txt    # (created automatically or copied)
└── output/           # (your output files)
```

### Credentials file format

```
site=https://www.linzwiki.at/
user=your_username
password=your_password
```

- `site` is required for all operations
- `user` and `password` are only needed for update operations
- The batch file creates a template with comments

## Best Practices

### For Developers

1. **Always test before releasing** - Run `mvn test` to ensure everything works
2. **Use semantic versioning** - `MAJOR.MINOR.PATCH` (e.g., `1.0.0`)
3. **Document breaking changes** - Update `VERSION.txt` with important notes
4. **Don't delete old releases** - Keep them for projects that depend on specific versions
5. **Include the batch file** - Always copy `mwcli.bat` to releases

### For External Projects

1. **Use the batch file** - It handles paths and credentials automatically
2. **Copy entire release directory** - Don't just copy the JAR file
3. **Edit credentials in your working directory** - Don't modify release files
4. **Use specific versions for production** - Prefer `v1.0` over `latest` for stability
5. **Check VERSION.txt** - For any important notes or breaking changes

## Versioning Strategy

Use [Semantic Versioning](https://semver.org/):

- **MAJOR version** when you make incompatible API changes
- **MINOR version** when you add functionality in a backwards-compatible manner
- **PATCH version** when you make backwards-compatible bug fixes

Examples:
- `1.0.0` - First stable release
- `1.0.1` - Bug fix release
- `1.1.0` - New features added (e.g., search functionality)
- `2.0.0` - Breaking changes

## What's Included in Release 1.0

✅ **All Core Features:**
- Read pages (`--read`)
- Read categories (`--read-category`)
- Update pages (`--update`)
- Search pages (`--search`)

✅ **Output Options:**
- Console output (formatted)
- File output (`--file` option)
- UTF-8 support

✅ **Convenience Features:**
- Batch file (`mwcli.bat`)
- Automatic credentials creation
- Error handling
- Help system

✅ **Documentation:**
- VERSION.txt
- README.txt
- CREDENTIALS.template.txt

## Troubleshooting

### "JAR file not found" error

**Cause:** The batch file can't find the JAR file.

**Solution:** Make sure you copied the entire release directory, not just the batch file.

```batch
REM Correct: Copy entire directory
xcopy "release\v1.0" "your_project\cli" /E

REM Wrong: Copy only batch file
copy "release\v1.0\mwcli.bat" "your_project"
```

### Credentials not working

**Cause:** The credentials file might be in the wrong location or have incorrect format.

**Solution:** 
1. Make sure `CREDENTIALS.txt` is in your **current working directory**
2. Edit the file with your actual MediaWiki credentials
3. For updates, you need both `user` and `password`

### Character encoding issues

**Cause:** Windows console has limited UTF-8 support.

**Solution:** Use file output for special characters:
```batch
mwcli.bat --read "PageWithUmlauts" --file output.txt
```

## Advanced: Automated Releases

For CI/CD pipelines, you can automate releases:

```batch
REM In your CI script:
if "%BRANCH%" == "main" (
    REM Build and test
    call mvn clean test
    
    REM Extract version (or use timestamp)
    set VERSION=1.0.%DATE:~10,4%%DATE:~4,2%%DATE:~7,2%
    
    REM Create release
    mkdir release\v%VERSION%
    copy target\mediawiki-cli-*.jar "release\v%VERSION%\mediawiki-cli-%VERSION%.jar"
    copy README.md "release\v%VERSION%\"
    copy CREDENTIALS.template.txt "release\v%VERSION%\"
    copy mwcli.bat "release\v%VERSION%\"
    
    REM Create version info
    echo MediaWiki CLI %VERSION% > "release\v%VERSION%\VERSION.txt"
    echo Released: %DATE% %TIME% >> "release\v%VERSION%\VERSION.txt"
    
    REM Update latest
    robocopy release\v%VERSION% release\latest /MIR
    
    REM Tag the release
    git tag v%VERSION%
    git push origin v%VERSION%
)
```

## Summary

The release system provides:

✅ **Stability** - External projects use fixed versions that don't change
✅ **Isolation** - Development in `target/` doesn't affect production releases
✅ **Convenience** - Batch file handles paths and credentials automatically
✅ **Versioning** - Clear version history and rollback capability
✅ **Portability** - Easy to copy release directories to other machines
✅ **Self-contained** - Everything needed is included in the release

This approach allows you to continue development while maintaining stable releases for external consumption. External projects get a complete, working solution that's easy to integrate and use.