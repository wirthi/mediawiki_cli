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
            
            // Login if credentials are available
            if (credentials.containsKey("user") && credentials.containsKey("password")) {
                boolean loginSuccess = client.login(credentials.get("user"), credentials.get("password"));
                if (!loginSuccess) {
                    System.err.println("Warning: Login failed. Some operations may not work.");
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
        
        try {
            System.out.println("Querying page: " + pageName);
            String pageContent = client.queryPage(pageName);
            
            if (pageContent != null) {
                System.out.println("\nContent of page '" + pageName + "':");
                System.out.println(pageContent);
            } else {
                System.err.println("Error: Page '" + pageName + "' not found.");
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
                System.err.println("Error: Category '" + categoryName + "' not found or is empty.");
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
            System.err.println("Error: --update command requires a page name and content.");
            printUsage();
            return;
        }
        
        String pageName = args[1];
        String content = args[2];
        String summary = args.length > 3 ? args[3] : null;
        
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
     * Prints the usage information for the CLI tool.
     */
    private static void printUsage() {
        System.out.println("MediaWiki CLI Tool");
        System.out.println("==================");
        System.out.println("Usage: java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar <command> [arguments]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  --read <page_name>        Read and print the content of a page");
        System.out.println("  --read-category <category>  Read and print the list of pages in a category");
        System.out.println("  --update <page_name> <content> [summary]  Update a page with new content and optional edit summary");
        System.out.println("  --help                   Print this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read Hauptseite");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read-category Linz");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update TestPage \"This is new content\"");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --update TestPage \"Content\" \"My edit summary\"");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help");
    }
}
