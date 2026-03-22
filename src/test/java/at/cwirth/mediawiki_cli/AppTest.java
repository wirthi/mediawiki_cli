package at.cwirth.mediawiki_cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit test for the MediaWiki CLI App.
 */
public class AppTest 
    extends TestCase
{
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    @Override
    protected void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Override
    protected void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Test the --help command.
     */
    public void testHelpCommand() {
        String[] args = {"--help"};
        App.main(args);
        
        String output = outContent.toString();
        assertTrue("Output should contain usage information", output.contains("Usage:"));
        assertTrue("Output should contain --read command", output.contains("--read"));
        assertTrue("Output should contain --help command", output.contains("--help"));
    }

    /**
     * Test the --read command with a valid page name.
     */
    public void testReadCommand() {
        String[] args = {"--read", "Hauptseite"};
        App.main(args);
        
        String output = outContent.toString();
        // Check for either successful content or proper error handling
        boolean success = output.contains("Content of page 'Hauptseite':");
        boolean errorHandled = output.contains("API Error:") || output.contains("Error: Page 'Hauptseite' not found");
        assertTrue("Output should contain either page content or proper error message", success || errorHandled);
    }

    /**
     * Test the --read command with a missing page name.
     */
    public void testReadCommandMissingPageName() {
        String[] args = {"--read"};
        App.main(args);
        
        String error = errContent.toString();
        assertTrue("Error should indicate missing page name", error.contains("--read command requires a page name"));
    }

    /**
     * Test an unknown command.
     */
    public void testUnknownCommand() {
        String[] args = {"--unknown"};
        App.main(args);
        
        String error = errContent.toString();
        assertTrue("Error should indicate unknown command", error.contains("Unknown command:"));
    }

    /**
     * Test with no arguments.
     */
    public void testNoArguments() {
        String[] args = {};
        App.main(args);
        
        String output = outContent.toString();
        assertTrue("Output should contain usage information", output.contains("Usage:"));
    }
    
    /**
     * Test the --read-category command with a valid category name.
     */
    public void testReadCategoryCommand() {
        String[] args = {"--read-category", "Linz"};
        App.main(args);
        
        String output = outContent.toString();
        // Check for either successful category listing or proper error handling
        boolean success = output.contains("Pages in category 'Linz':");
        boolean errorHandled = output.contains("API Error:") || output.contains("Error: Category 'Linz' not found");
        assertTrue("Output should contain either category members or proper error message", success || errorHandled);
    }
    
    /**
     * Test the --read-category command with --file option.
     */
    public void testReadCategoryCommandWithFile() {
        String[] args = {"--read-category", "Linz", "--file", "test_category_output.txt"};
        App.main(args);
        
        String output = outContent.toString();
        // Should either succeed with file output or handle error properly
        boolean success = output.contains("Category members saved to:");
        boolean errorHandled = output.contains("API Error:") || output.contains("Error: Category 'Linz' not found");
        assertTrue("Output should contain either file save confirmation or proper error message", success || errorHandled);
        
        // Clean up test file if it was created
        java.io.File testFile = new java.io.File("test_category_output.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    /**
     * Test the --read-category command with a missing category name.
     */
    public void testReadCategoryCommandMissingCategoryName() {
        String[] args = {"--read-category"};
        App.main(args);
        
        String error = errContent.toString();
        assertTrue("Error should indicate missing category name", error.contains("--read-category command requires a category name"));
    }
    
    /**
     * Test the --search command.
     */
    public void testSearchCommand() {
        String[] args = {"--search", "Linz"};
        App.main(args);
        
        String output = outContent.toString();
        // Check for either successful search results or proper error handling
        boolean success = output.contains("Search results for 'Linz':");
        boolean noResults = output.contains("No results found for:");
        boolean errorHandled = output.contains("API Error:") || output.contains("Error searching pages:");
        assertTrue("Output should contain either search results, no results message, or proper error message", 
                  success || noResults || errorHandled);
    }
    
    /**
     * Test the --search command with --file option.
     */
    public void testSearchCommandWithFile() {
        String[] args = {"--search", "Linz", "--file", "test_search_output.txt"};
        App.main(args);
        
        String output = outContent.toString();
        // Should either succeed with file output or handle error properly
        boolean success = output.contains("Search results saved to:");
        boolean noResults = output.contains("No results found for:");
        boolean errorHandled = output.contains("API Error:") || output.contains("Error searching pages:");
        assertTrue("Output should contain either file save confirmation, no results message, or proper error message", 
                  success || noResults || errorHandled);
        
        // Clean up test file if it was created
        java.io.File testFile = new java.io.File("test_search_output.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}