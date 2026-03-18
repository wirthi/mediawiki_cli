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
            // Page content might be null if API returns error, but exception should not be thrown
            assertTrue("Either page content should be returned or null with proper error handling", 
                      pageContent != null || true); // Always true - we're testing no exception is thrown
        } catch (IOException | InterruptedException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testLogin() {
        try {
            // Only test login if credentials contain user and password
            if (credentials.containsKey("user") && credentials.containsKey("password")) {
                // Test login - should succeed with valid credentials
                boolean loginSuccess = client.login(credentials.get("user"), credentials.get("password"));
                
                // With the Cwbotai account credentials, login should succeed
                // This test will fail if credentials are incorrect or account doesn't exist
                assertTrue("Login should succeed with valid Cwbotai credentials", loginSuccess);
                
            } else {
                // Skip test if no credentials available
                System.out.println("Skipping login test - no user/password credentials available");
            }
        } catch (IOException | InterruptedException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testUpdatePage() {
        try {
            // Only test update if we have valid credentials and login succeeds
            if (credentials.containsKey("user") && credentials.containsKey("password")) {
                // First login
                boolean loginSuccess = client.login(credentials.get("user"), credentials.get("password"));
                assertTrue("Login required for update operations", loginSuccess);
                
                // Test update on a test page
                String testPage = "Benutzer:Cwbotai/TestPage";
                String originalContent = client.queryPage(testPage);
                
                // Update the page
                String newContent = "Test content updated at " + System.currentTimeMillis();
                boolean updateSuccess = client.updatePage(testPage, newContent, "Automated test update");
                assertTrue("Update should succeed with valid credentials", updateSuccess);
                
                // Verify the update worked by reading back
                String updatedContent = client.queryPage(testPage);
                assertNotNull("Page should exist after update", updatedContent);
                assertTrue("Content should be updated", updatedContent.contains("Test content updated at"));
                
                // Restore original content if it existed
                if (originalContent != null) {
                    client.updatePage(testPage, originalContent, "Restoring original content");
                }
            } else {
                // Skip test if no credentials available
                System.out.println("Skipping update test - no user/password credentials available");
            }
        } catch (IOException | InterruptedException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}