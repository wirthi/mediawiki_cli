# AGENTS.md - MediaWiki CLI Project

## Overview
This document outlines the setup and guidelines for working with the MediaWiki CLI project, a Java and Maven-based command-line tool for querying and updating pages on MediaWiki-based websites.

## Project Structure
- **Language**: Java
- **Build Tool**: Maven
- **Purpose**: Command-line tool for MediaWiki interaction

## Key Features (Planned)
- Query MediaWiki pages
- Update MediaWiki pages
- Support for authentication
- Command-line interface

## Development Environment
- **Java Version**: [Specify Java version, e.g., Java 8+]
- **Maven Version**: [Specify Maven version, e.g., Maven 3.6+]
- **IDE**: Eclipse is currently used, but IntelliJ IDEA or VS Code are reasonable alternatives.

## Getting Started
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd mediawiki-cli
   ```

2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Run Tests**:
   ```bash
   mvn test
   ```

## Maven Commands
- **Clean and Build**: `mvn clean install`
- **Run Tests**: `mvn test`
- **Generate Javadoc**: `mvn javadoc:javadoc`
- **Create JAR**: `mvn package`

## Git Workflow
- **Branching**: Use feature branches for new features or bug fixes.
- **Commits**: Write clear and descriptive commit messages.
- **Pull Requests**: Submit pull requests for review before merging.

## Code Style
- Follow standard Java coding conventions.
- Use meaningful variable and method names.
- Include comments for complex logic.

## Dependencies
- **Command-Line Parsing**: args4j

## Testing
- **Unit Tests**: Use JUnit for unit testing.
- **Integration Tests**: Test interactions with the MediaWiki API.
- **Mocking**: Use Mockito for mocking dependencies.

## Documentation
- **Javadoc**: Document public methods and classes.
- **README**: Provide an overview of the project and usage instructions.
- **AGENTS.md**: This file, for development guidelines.

## Contributing
- Fork the repository.
- Create a feature branch.
- Submit a pull request.

## License
MIT

## Contact
Christian Wirth, wirthi@gmx.at
