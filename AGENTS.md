# AGENTS.md - MediaWiki CLI Project

## Overview
This document outlines the setup and guidelines for working with the MediaWiki CLI project, a Java and Maven-based command-line tool for querying and updating pages on MediaWiki-based websites.

## Project Structure
- **Language**: Java
- **Build Tool**: Maven
- **Purpose**: Command-line tool for MediaWiki interaction

## Key Features
- ✅ Query MediaWiki pages (`--read`)
- ✅ Read category members (`--read-category`)
- ✅ Update MediaWiki pages with edit summaries (`--update`)
- ✅ Automatic authentication with credentials file
- ✅ Command-line interface with help system

## Development Environment
- **Java Version**: Java 17+ (tested with GraalVM JDK 17.0.11)
- **Maven Version**: Maven 3.9.8+
- **IDE**: Eclipse/IntelliJ IDEA/VS Code with Java extensions
- **OS**: Windows 11 (but should work on Linux/macOS)

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
- **HTTP Client**: Java 11+ HttpClient (built-in)
- **JSON Processing**: Jackson Databind 2.15.2
- **Testing**: JUnit 4.13.2
- **Build**: Maven Assembly Plugin for fat JAR

## Testing
- **Unit Tests**: JUnit 4 for core functionality testing
- **Integration Tests**: Live tests against MediaWiki API (requires credentials)
- **Test Coverage**: Focus on happy paths and error handling
- **Mocking**: Consider Mockito for future mocking needs

## MediaWiki API Insights

### Authentication Flow
1. **Login Token**: Required for authentication (`action=query&meta=tokens&type=login`)
2. **Session Cookies**: Must be maintained across requests
3. **CSRF Token**: Required for edit operations (`action=query&meta=tokens&type=csrf`)

### Edit API Parameters
- `action=edit`: Main edit endpoint
- `title`: Page title (required)
- `text`: New page content (required)
- `token`: CSRF token (required)
- `summary`: Edit summary (optional but recommended)
- `format=json`: Response format

### Success Response Structure
- **Login Success**: `response.login.result === "Success"`
- **Edit Success**: `response.edit.result === "Success"`
- **Errors**: `response.error.info` contains error messages

## Command Line Design Patterns

### Argument Handling
- Use array indexing for simple argument parsing
- Support optional parameters with array length checks
- Provide clear error messages for missing arguments

### User Feedback
- Show progress messages during operations
- Display success/failure clearly
- Include helpful error details when available

## Windows Batch Script Tips

### mwcli.bat Best Practices
- Check for JAR existence and build if missing
- Create default config files if absent
- Use `%*` to pass all arguments to Java
- Use `setlocal`/`endlocal` for environment isolation

### UTF-8 Console Limitations
- Windows `cmd.exe` has limited UTF-8 support even with `chcp 65001`
- Special characters may display incorrectly in console output
- **Data integrity is preserved**: UTF-8 works correctly for API communication
- For full UTF-8 display, consider:
  - Using PowerShell instead of cmd.exe
  - Redirecting output to files
  - Using Windows Terminal with proper font support
  - Testing with `System.setProperty("file.encoding", "UTF-8")`

## Documentation
- **Javadoc**: Document public methods and classes (needs improvement)
- **README**: User-facing documentation with examples
- **AGENTS.md**: Development guidelines and technical insights
- **Code Comments**: Focus on complex logic and API interactions

## Lessons Learned

### Java HTTP Client
- Use `HttpClient.newBuilder().cookieHandler(new CookieManager())` for session persistence
- `BodyPublishers.ofString(queryString, StandardCharsets.UTF_8)` for explicit UTF-8 POST data
- `BodyHandlers.ofString(StandardCharsets.UTF_8)` for explicit UTF-8 response handling
- Always check response bodies for errors even on 200 status codes

### JSON Processing with Jackson
- Use `ObjectMapper` for flexible JSON parsing
- `JsonNode.path("key").asText()` with null checks for safe access
- Handle both success and error response structures

### MediaWiki API Quirks
- Different success paths for different actions (login vs edit)
- Token requirements vary by operation type
- Error responses may be nested differently

### Backward Compatibility
- Use method overloading for optional parameters
- Maintain existing signatures when adding features
- Document breaking changes clearly

## Future Enhancements

### Potential Features
- Batch operations for multiple pages
- Page creation vs update detection
- Minor edit flag support
- Bot flag for automated edits
- Diff viewing before updating

### Code Improvements
- Add proper method overloading for update operations
- Improve error handling with custom exceptions
- Add more comprehensive logging
- Implement proper argument parsing library (e.g., args4j)
- Add configuration file support beyond credentials
- **UTF-8 improvements**: Investigate better Windows console output handling

## Contributing
- Fork the repository.
- Create a feature branch.
- Submit a pull request.

## License
MIT

## Contact
Christian Wirth, wirthi@gmx.at
