package at.cwirth.mediawiki_cli;

import java.io.IOException;
import java.util.Map;

import at.cwirth.mediawiki_cli.util.CredentialsReader;

/**
 * Main application class for the MediaWiki CLI tool.
 * This class provides a command-line interface for interacting with a MediaWiki site.
 */
public class App 
{
    private static final String COMMAND_READ = "--read";
    private static final String COMMAND_READ_CATEGORY = "--read-category";
    private static final String COMMAND_UPDATE = "--update";
    private static final String COMMAND_HELP = "--help";
    
    public static void main(String[] args)
    {
        // Set UTF-8 encoding for console output to handle special characters correctly
        System.setProperty("file.encoding", "UTF-8");
        
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String command = args[0];
        
        try {
            Map<String, String> credentials = CredentialsReader.readCredentials();
            MediaWikiClient client = new MediaWikiClient(credentials.get("site"));
            
            // Only login for commands that require authentication (update)
            boolean needsAuth = command.equals(COMMAND_UPDATE);
            
            if (needsAuth && credentials.containsKey("user") && credentials.containsKey("password")) {
                boolean loginSuccess = client.login(credentials.get("user"), credentials.get("password"));
                if (!loginSuccess) {
                    System.err.println("Warning: Login failed. Update operations will not work.");
                }
            }
            
            switch (command) {
                case COMMAND_READ:
                    handleReadCommand(args, client);
                    break;
                case COMMAND_READ_CATEGORY:
                    handleReadCategoryCommand(args, client);
                    break;
                case COMMAND_UPDATE:
                    handleUpdateCommand(args, client);
                    break;
                case COMMAND_HELP:
                    printUsage();
                    break;
                default:
                    System.err.println("Unknown command: " + command);
                    printUsage();
            }
        } catch (IOException e) {
            System.err.println("Error reading credentials: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
    }
    
    /**
     * Handles the --read command.
     *
     * @param args The command-line arguments.
     * @param client The MediaWiki client.
     */
    private static void handleReadCommand(String[] args, MediaWikiClient client) {
        if (args.length < 2) {
            System.err.println("Error: --read command requires a page name.");
            printUsage();
            return;
        }
        
        String pageName = args[1];
        String outputFile = null;
        
        // Check for --file option
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("--file") && i + 1 < args.length) {
                outputFile = args[i + 1];
                i++; // Skip the filename in next iteration
            }
        }
        
        try {
            System.out.println("Querying page: " + pageName);
            String pageContent = client.queryPage(pageName);
            
            if (pageContent != null) {
                if (outputFile != null) {
                    // Write to file - only the raw content, no headers
                    try (java.io.PrintWriter writer = new java.io.PrintWriter(outputFile, "UTF-8")) {
                        writer.print(pageContent);
                        System.out.println("Page content saved to: " + outputFile);
                    } catch (java.io.FileNotFoundException e) {
                        System.err.println("Error writing to file: " + e.getMessage());
                    } catch (java.io.UnsupportedEncodingException e) {
                        System.err.println("Error: UTF-8 encoding not supported: " + e.getMessage());
                    }
                } else {
                    // Print to console
                    System.out.println("\nContent of page '" + pageName + "':");
                    System.out.println(pageContent);
                }
            } else {
                System.out.println("Error: Page '" + pageName + "' not found.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error querying page: " + e.getMessage());
        }
    }
    
    /**
     * Handles the --read-category command.
     *
     * @param args The command-line arguments.
     * @param client The MediaWiki client.
     */
    private static void handleReadCategoryCommand(String[] args, MediaWikiClient client) {
        if (args.length < 2) {
            System.err.println("Error: --read-category command requires a category name.");
            printUsage();
            return;
        }
        
        String categoryName = args[1];
        
        try {
            System.out.println("Querying category: " + categoryName);
            String[] pageTitles = client.queryCategory(categoryName);
            
            if (pageTitles != null && pageTitles.length > 0) {
                System.out.println("\nPages in category '" + categoryName + "':");
                for (String title : pageTitles) {
                    System.out.println("- " + title);
                }
            } else {
                System.out.println("Error: Category '" + categoryName + "' not found or is empty.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error querying category: " + e.getMessage());
        }
    }
    
    /**
     * Handles the --update command.
     *
     * @param args The command-line arguments.
     * @param client The MediaWiki client.
     */
    private static void handleUpdateCommand(String[] args, MediaWikiClient client) {
        if (args.length < 3) {
            System.err.println("Error: --update command requires a page name and --content or --file option.");
            printUsage();
            return;
        }
        
        String pageName = args[1];
        String content = null;
        String summary = null;
        
        // Parse arguments to find --content, --file, and --summary
        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("--content") && i + 1 < args.length) {
                content = args[i + 1];
                i++; // Skip the content in next iteration
            } else if (args[i].equals("--file") && i + 1 < args.length) {
                // Read content from file
                try {
                    content = readFileContent(args[i + 1]);
                    i++; // Skip the filename in next iteration
                } catch (IOException e) {
                    System.err.println("Error reading file: " + e.getMessage());
                    return;
                }
            } else if (args[i].equals("--summary") && i + 1 < args.length) {
                summary = args[i + 1];
                i++; // Skip the summary in next iteration
            }
        }
        
        // Validate that we have content
        if (content == null) {
            System.err.println("Error: --update command requires either --content or --file option.");
            printUsage();
            return;
        }
        
        try {
            System.out.println("Updating page: " + pageName);
            if (summary != null) {
                System.out.println("Edit summary: " + summary);
            }
            boolean success = client.updatePage(pageName, content, summary);
            
            if (success) {
                System.out.println("Page '" + pageName + "' updated successfully.");
            } else {
                System.err.println("Error: Failed to update page '" + pageName + "'.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error updating page: " + e.getMessage());
        }
    }
    
    /**
     * Reads content from a file with UTF-8 encoding.
     *
     * @param filePath The path to the file.
     * @return The file content as a string.
     * @throws IOException If an error occurs while reading the file.
     */
    private static String readFileContent(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
    
    /**
     * Prints the usage information for the CLI tool.
     */
    private static void printUsage() {
        System.out.println("MediaWiki CLI Tool");
        System.out.println("==================");
        System.out.println("Usage: java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar <command> [arguments]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  --read <page_name> [--file <filename>]          Read page content (optionally save to file)");
        System.out.println("  --read-category <category>                       Read and print the list of pages in a category");
        System.out.println("  --update <page_name> (--content <text> | --file <filename>) [--summary <text>]");
        System.out.println("                                                  Update a page with content from text or file");
        System.out.println("  --help                                            Print this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read Hauptseite");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read Hauptseite --file output.txt");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read-category Linz");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update TestPage --content \"This is new content\"");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update TestPage --file content.txt --summary \"My summary\"");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help");
    }
}
