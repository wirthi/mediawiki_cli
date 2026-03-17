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

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

For questions or support, please contact the project maintainer.
