# MediaWiki CLI Tool

A command-line tool for interacting with MediaWiki sites. This tool allows you to read and manage pages on a MediaWiki site using the MediaWiki API.

## Features

- Read page content from a MediaWiki site
- Simple and intuitive command-line interface
- Supports authentication for private wikis

## Prerequisites

- Java 8 or higher
- Maven (for building from source)

## Installation

### From Source

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd mediawiki-cli
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. The fat JAR file will be created in the `target` directory:
   ```bash
   target/mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar
   ```

## Configuration

Before using the tool, you need to configure the credentials for your MediaWiki site. Create a file named `CREDENTIALS.txt` in the same directory as the JAR file with the following content:

```
site=https://your-mediawiki-site.com/w/
user=your-username
password=your-password
```

Replace the placeholders with your actual MediaWiki site URL, username, and password.

**Note:** The credentials file is read using UTF-8 encoding, so you can use special characters in passwords if needed.

**Important:** When creating or editing the credentials file on Windows:
- Use a proper text editor that supports UTF-8 (Notepad++, VS Code, etc.)
- Avoid using `echo` or `type` commands which use ANSI encoding by default
- Save the file with UTF-8 encoding (without BOM for best compatibility)

## Usage

### Read a Page

To read the content of a page, use the `--read` command followed by the page name:

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read "Page Name"
```

**Example:**
```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read Hauptseite
```

### Read a Category

To read the list of pages in a category, use the `--read-category` command followed by the category name:

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read-category "Category Name"
```

**Example:**
```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read-category Linz
```

### Update a Page

To update the content of a page, use the `--update` command followed by the page name and the new content. You can optionally provide an edit summary as a third argument:

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update "Page Name" "New content" ["Edit summary"]
```

**Examples:**

Basic update without summary:
```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update "Benutzer:YourName/TestPage" "This is my updated content."
```

Update with edit summary (supports UTF-8):
```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update "Benutzer:YourName/TestPage" "Fixed typo" "Typo correction"
```

Update with German umlauts:
```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update "Benutzer:YourName/TestPage" "Updated content with umlauts: äöüß" "Added German characters"
```

**Note:** You need to have valid credentials in your `CREDENTIALS.txt` file to update pages. Edit summaries are optional but recommended for better tracking of changes.

**UTF-8 Support:** The tool fully supports UTF-8 encoding for all text operations. However, display of special characters in the Windows console may be limited by the system's font and encoding settings.

### Print Help

To print the help message, use the `--help` command:

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help
```

## Examples

### Read the Main Page

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read Hauptseite
```

### Read a Category

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read-category Linz
```

### Update a Page

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update "Benutzer:YourName/TestPage" "Updated content"
```

### Print Help Message

```bash
java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help
```

## Building from Source

If you want to build the project from source, follow these steps:

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd mediawiki-cli
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. Run the tests:
   ```bash
   mvn test
   ```

4. The fat JAR file will be created in the `target` directory:
   ```bash
   target/mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar
   ```

## Encoding and Character Support

### UTF-8 Support

The MediaWiki CLI tool fully supports UTF-8 encoding for:

- **API Communication**: All requests and responses use UTF-8
- **File I/O**: Credentials and configuration files are read/written in UTF-8
- **Text Content**: Page content and edit summaries support UTF-8 characters
- **International Characters**: German umlauts (äöüß), accented characters (éèê), and other European scripts

### Windows Console Limitations

While the tool internally handles UTF-8 correctly, the Windows command prompt (`cmd.exe`) has limitations:

- **Character Display**: Special characters may not display correctly in the console
- **Data Integrity**: The actual data transmitted to/from the MediaWiki API is correct
- **Workarounds**:
  - Use PowerShell instead of cmd.exe
  - Redirect output to a file: `mwcli.bat --read PageName > output.txt`
  - Use Windows Terminal with UTF-8 support
  - Set console code page: `chcp 65001` before running

### Verifying Data Integrity

To verify that UTF-8 characters are being handled correctly:

1. **Update a page with special characters**:
   ```bash
   mwcli.bat --update "TestPage" "Test content: äöüß" "UTF-8 test"
   ```

2. **Check the page in your browser**: The characters should display correctly in the web interface

3. **Compare with API response**: The raw JSON from the MediaWiki API contains proper Unicode escape sequences

### Character Support Matrix

| Character Type | CLI Support | Console Display | Web Display |
|---------------|-------------|----------------|-------------|
| German umlauts (äöüß) | ✅ Yes | ⚠️ Limited | ✅ Yes |
| Accented characters (éèê) | ✅ Yes | ⚠️ Limited | ✅ Yes |
| Basic punctuation | ✅ Yes | ✅ Yes | ✅ Yes |
| Emoji/special symbols | ✅ Yes | ❌ No | ⚠️ Depends on wiki config |
| CJK characters | ✅ Yes | ❌ No | ⚠️ Depends on wiki config |

## Troubleshooting

### Login Issues

If you encounter login issues, ensure that:
- The credentials in `CREDENTIALS.txt` are correct.
- The user has the necessary permissions to access the MediaWiki API.
- The site URL is correct and includes the `/w/` path if required.

### Page Not Found

If a page is not found, ensure that:
- The page name is spelled correctly.
- The page exists on the MediaWiki site.
- You have the necessary permissions to view the page.

### Encoding Issues

If special characters don't display correctly:
- This is likely a Windows console limitation, not a tool issue
- The data is stored correctly on the wiki (check in your browser)
- Try redirecting output to a file or using PowerShell
- Set console code page: `chcp 65001`

**Note about file encoding:** Windows command-line tools (`echo`, `type`, file redirection) use ANSI encoding by default. For proper UTF-8 file operations:
- Use Java applications or proper text editors
- Our tool handles UTF-8 correctly internally
- File encoding issues typically come from external tools, not our application

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

For questions or support, please contact the project maintainer.
