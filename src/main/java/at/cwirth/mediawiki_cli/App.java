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
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String command = args[0];
        
        try {
            Map<String, String> credentials = CredentialsReader.readCredentials();
            MediaWikiClient client = new MediaWikiClient(credentials.get("site"));
            
            switch (command) {
                case COMMAND_READ:
                    handleReadCommand(args, client);
                    break;
                case COMMAND_READ_CATEGORY:
                    handleReadCategoryCommand(args, client);
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
        System.out.println("  --help                   Print this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read Hauptseite");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --read-category Linz");
        System.out.println("  java -jar mediawiki-cli-0.0.1-SNAPSHOT-jar-with-dependencies.jar --help");
    }
}
