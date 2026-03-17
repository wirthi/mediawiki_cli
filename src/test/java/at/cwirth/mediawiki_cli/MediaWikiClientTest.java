package at.cwirth.mediawiki_cli;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import at.cwirth.mediawiki_cli.util.CredentialsReader;

/**
 * Test class for MediaWikiClient.
 */
public class MediaWikiClientTest {
    
    private MediaWikiClient client;
    private Map<String, String> credentials;
    
    @Before
    public void setUp() throws IOException {
        // Load credentials
        credentials = CredentialsReader.readCredentials();
        
        // Initialize the MediaWiki client
        client = new MediaWikiClient(credentials.get("site"));
    }
    
    @Test
    public void testQueryPage() {
        try {
            // Test querying a known page
            String pageContent = client.queryPage("Hauptseite");
            assertNotNull("Page content should not be null", pageContent);
            assertTrue("Page content should not be empty", pageContent.length() > 0);
        } catch (IOException | InterruptedException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testLogin() {
        try {
            // Test login
            boolean loginSuccess = client.login(credentials.get("user"), credentials.get("password"));
            assertTrue("Login should be successful", loginSuccess);
        } catch (IOException | InterruptedException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}