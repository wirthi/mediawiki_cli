package at.cwirth.mediawiki_cli.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for reading and parsing credentials from a file.
 */
public class CredentialsReader {
    
    /**
     * Reads a credentials file and parses it into a HashMap.
     *
     * @param filePath The path to the credentials file.
     * @return A HashMap containing the key-value pairs from the file.
     * @throws IOException If an error occurs while reading the file.
     */
    public static Map<String, String> readCredentials(String filePath) throws IOException {
        Map<String, String> credentials = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Parse key-value pairs
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    credentials.put(key, value);
                }
            }
        }
        
        return credentials;
    }
    
    /**
     * Reads credentials from the default file path (CREDENTIALS.txt).
     *
     * @return A HashMap containing the key-value pairs from the file.
     * @throws IOException If an error occurs while reading the file.
     */
    public static Map<String, String> readCredentials() throws IOException {
        return readCredentials("CREDENTIALS.txt");
    }
}