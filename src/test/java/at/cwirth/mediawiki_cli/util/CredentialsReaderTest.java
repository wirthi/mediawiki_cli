package at.cwirth.mediawiki_cli.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

/**
 * Test class for CredentialsReader.
 */
public class CredentialsReaderTest {
    
    @Test
    public void testReadCredentials() {
        try {
            Map<String, String> credentials = CredentialsReader.readCredentials("CREDENTIALS.txt");
            
            // Verify that the credentials map is not null
            assertNotNull("Credentials map should not be null", credentials);
            
            // Verify that the expected keys are present
            assertTrue("Credentials map should contain 'site' key", credentials.containsKey("site"));
            assertTrue("Credentials map should contain 'user' key", credentials.containsKey("user"));
            assertTrue("Credentials map should contain 'password' key", credentials.containsKey("password"));
            
            // Verify the values (optional, if you want to check specific values)
            assertEquals("https://www.linzwiki.at/w/", credentials.get("site"));
            assertEquals("Cwbotai@Cwbotai", credentials.get("user"));            
        } catch (IOException e) {
            fail("IOException should not be thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testReadCredentialsDefaultPath() {
        try {
            Map<String, String> credentials = CredentialsReader.readCredentials();
            
            // Verify that the credentials map is not null
            assertNotNull("Credentials map should not be null", credentials);
            
            // Verify that the expected keys are present
            assertTrue("Credentials map should contain 'site' key", credentials.containsKey("site"));
            assertTrue("Credentials map should contain 'user' key", credentials.containsKey("user"));
            assertTrue("Credentials map should contain 'password' key", credentials.containsKey("password"));
            
        } catch (IOException e) {
            fail("IOException should not be thrown: " + e.getMessage());
        }
    }
}