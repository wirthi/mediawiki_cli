package at.cwirth.mediawiki_cli;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A client for interacting with the MediaWiki API.
 * This class provides methods to query and update pages on a MediaWiki site.
 */
public class MediaWikiClient {
    
    private final String apiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String token;
    
    /**
     * Constructs a MediaWikiClient for the given site URL.
     *
     * @param siteUrl The base URL of the MediaWiki site (e.g., "https://www.linzwiki.at/").
     */
    public MediaWikiClient(String siteUrl) {
        this.apiUrl = siteUrl + "api.php";
        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Logs in to the MediaWiki site using the provided credentials.
     *
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @return true if login was successful, false otherwise.
     * @throws IOException If an error occurs during the login process.
     * @throws InterruptedException If the login process is interrupted.
     */
    public boolean login(String username, String password) throws IOException, InterruptedException {
        System.out.println("Starting login process for user: " + username);
        
        // Step 1: Get a login token
        System.out.println("Step 1: Requesting login token...");
        Map<String, String> params = Map.of(
            "action", "query",
            "meta", "tokens",
            "type", "login",
            "format", "json"
        );
        String response = sendGetRequest(params);
        
        // Parse the token from the response
        this.token = extractToken(response, "logintoken");
        
        if (this.token == null) {
            System.err.println("Failed to retrieve login token. Check the API response for errors.");
            return false;
        }
        
        System.out.println("Login token retrieved successfully.");
        
        // Step 2: Send the login request with the token
        System.out.println("Step 2: Sending login request...");
        params = Map.of(
            "action", "login",
            "lgname", username,
            "lgpassword", password,
            "lgtoken", token,
            "format", "json"
        );
        response = sendPostRequest(params);
        
        System.out.println("Login response received.");
        
        // Check if login was successful
        boolean success = isSuccess(response);
        if (!success) {
            System.err.println("Login failed. Check the credentials and API response for errors.");
            printLoginErrorDetails(response);
        } else {
            System.out.println("Login successful!");
        }
        
        return success;
    }
    
    /**
     * Prints detailed error information from the login response.
     *
     * @param response The login response from the API.
     */
    private void printLoginErrorDetails(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode login = root.path("login");
            
            if (login.has("result")) {
                System.err.println("Login result: " + login.path("result").asText());
            }
            
            if (login.has("reason")) {
                System.err.println("Login reason: " + login.path("reason").asText());
            }
            
            if (root.has("error")) {
                System.err.println("API Error: " + root.path("error").path("info").asText());
            }
            
            System.err.println("Full response: " + response);
        } catch (Exception e) {
            System.err.println("Error parsing login response: " + e.getMessage());
        }
    }
    
    /**
     * Queries a page from the MediaWiki site.
     *
     * @param pageTitle The title of the page to query.
     * @return The content of the page, or null if the page does not exist or an error occurs.
     * @throws IOException If an error occurs during the query process.
     * @throws InterruptedException If the query process is interrupted.
     */
    public String queryPage(String pageTitle) throws IOException, InterruptedException {
        Map<String, String> params = Map.of(
            "action", "query",
            "prop", "revisions",
            "titles", pageTitle,
            "rvprop", "content",
            "format", "json"
        );
        String response = sendGetRequest(params);
        
        // Parse the page content from the response
        return extractPageContent(response);
    }
    
    /**
     * Updates a page on the MediaWiki site.
     *
     * @param pageTitle The title of the page to update.
     * @param content The new content for the page.
     * @return true if the update was successful, false otherwise.
     * @throws IOException If an error occurs during the update process.
     * @throws InterruptedException If the update process is interrupted.
     */
    public boolean updatePage(String pageTitle, String content) throws IOException, InterruptedException {
        // Step 1: Get an edit token
        Map<String, String> params = Map.of(
            "action", "query",
            "meta", "tokens",
            "type", "csrf",
            "format", "json"
        );
        String response = sendGetRequest(params);
        
        // Parse the token from the response
        String editToken = extractToken(response, "csrftoken");
        
        if (editToken == null) {
            System.err.println("Failed to retrieve edit token.");
            return false;
        }
        
        // Step 2: Send the edit request
        params = Map.of(
            "action", "edit",
            "title", pageTitle,
            "text", content,
            "token", editToken,
            "format", "json"
        );
        response = sendPostRequest(params);
        
        // Check if the edit was successful
        return isSuccess(response);
    }
    
    /**
     * Sends a GET request to the MediaWiki API.
     *
     * @param params The parameters for the API request.
     * @return The response from the API.
     * @throws IOException If an error occurs during the request.
     * @throws InterruptedException If the request is interrupted.
     */
    private String sendGetRequest(Map<String, String> params) throws IOException, InterruptedException {
        String queryString = buildQueryString(params);
        String url = apiUrl + "?" + queryString;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return httpResponse.body();
    }
    
    /**
     * Sends a POST request to the MediaWiki API.
     *
     * @param params The parameters for the API request.
     * @return The response from the API.
     * @throws IOException If an error occurs during the request.
     * @throws InterruptedException If the request is interrupted.
     */
    private String sendPostRequest(Map<String, String> params) throws IOException, InterruptedException {
        String queryString = buildQueryString(params);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(queryString))
                .build();
        
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return httpResponse.body();
    }
    
    /**
     * Builds a query string from a map of parameters.
     *
     * @param params The parameters to include in the query string.
     * @return The encoded query string.
     */
    private String buildQueryString(Map<String, String> params) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            queryString.append("=");
            queryString.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return queryString.toString();
    }
    
    /**
     * Extracts a token from the API response.
     *
     * @param response The API response.
     * @param tokenType The type of token to extract (e.g., "logintoken", "csrftoken").
     * @return The extracted token, or null if not found.
     */
    private String extractToken(String response, String tokenType) {
        try {
            JsonNode root = objectMapper.readTree(response);
            
            // Check for errors in the response
            if (root.has("error")) {
                System.err.println("API Error: " + root.path("error").path("info").asText());
                return null;
            }
            
            JsonNode query = root.path("query");
            JsonNode tokens = query.path("tokens");
            return tokens.path(tokenType).asText();
        } catch (Exception e) {
            System.err.println("Error parsing token from response: " + e.getMessage());
            System.err.println("Response: " + response);
            return null;
        }
    }
    
    /**
     * Queries the members of a category from the MediaWiki site.
     *
     * @param categoryName The name of the category to query.
     * @return A list of page titles in the category, or null if the category does not exist or an error occurs.
     * @throws IOException If an error occurs during the query process.
     * @throws InterruptedException If the query process is interrupted.
     */
    public String[] queryCategory(String categoryName) throws IOException, InterruptedException {
        Map<String, String> params = Map.of(
            "action", "query",
            "list", "categorymembers",
            "cmtitle", "Category:" + categoryName,
            "cmlimit", "500",
            "format", "json"
        );
        String response = sendGetRequest(params);
        
        // Parse the category members from the response
        return extractCategoryMembers(response);
    }
    
    /**
     * Extracts the page content from the API response.
     *
     * @param response The API response.
     * @return The extracted page content, or null if not found.
     */
    private String extractPageContent(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            
            // Check for errors in the response
            if (root.has("error")) {
                System.err.println("API Error: " + root.path("error").path("info").asText());
                return null;
            }
            
            JsonNode query = root.path("query");
            JsonNode pages = query.path("pages");
            
            // Get the first page
            JsonNode page = pages.elements().next();
            
            // Check if the page exists
            if (page.has("missing")) {
                System.err.println("Page does not exist.");
                return null;
            }
            
            JsonNode revisions = page.path("revisions");
            
            // Get the first revision
            JsonNode revision = revisions.elements().next();
            return revision.path("*").asText();
        } catch (Exception e) {
            System.err.println("Error parsing page content from response: " + e.getMessage());
            System.err.println("Response: " + response);
            return null;
        }
    }
    
    /**
     * Extracts the category members from the API response.
     *
     * @param response The API response.
     * @return An array of page titles in the category, or null if not found.
     */
    private String[] extractCategoryMembers(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            
            // Check for errors in the response
            if (root.has("error")) {
                System.err.println("API Error: " + root.path("error").path("info").asText());
                return null;
            }
            
            JsonNode query = root.path("query");
            JsonNode categoryMembers = query.path("categorymembers");
            
            // Collect the page titles
            java.util.List<String> pageTitles = new java.util.ArrayList<>();
            for (JsonNode member : categoryMembers) {
                pageTitles.add(member.path("title").asText());
            }
            
            return pageTitles.toArray(new String[0]);
        } catch (Exception e) {
            System.err.println("Error parsing category members from response: " + e.getMessage());
            System.err.println("Response: " + response);
            return null;
        }
    }
    
    /**
     * Checks if the API response indicates a successful operation.
     *
     * @param response The API response.
     * @return true if the operation was successful, false otherwise.
     */
    private boolean isSuccess(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.path("login").path("result");
            return "Success".equals(result.asText());
        } catch (Exception e) {
            System.err.println("Error checking success status: " + e.getMessage());
            return false;
        }
    }
}