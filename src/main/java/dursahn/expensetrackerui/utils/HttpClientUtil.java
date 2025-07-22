package dursahn.expensetrackerui.utils;

import com.google.gson.Gson;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientUtil {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final Gson gson = new Gson();

    private static final String baseUrl = "http://localhost:8080";

    public static HttpResponse<String> sendPostRequest(
            String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return client.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    public static void sendPostRequestWithToken(String path, String token, String jsonBody)
            throws IOException, InterruptedException, AuthenticationException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)  // Add Bearer token
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))  // Send the JSON body
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status code is 403, throw the custom AuthenticationException
        if (response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }
    }

    public static void sendPutRequestWithToken(String path, String token, String jsonBody)
            throws IOException, InterruptedException, AuthenticationException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)  // Add Bearer token
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))  // Send the JSON body
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status code is 403, throw the custom AuthenticationException
        if (response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }
    }

    public static String sendGetRequestWithToken(String path, String token)
            throws IOException, InterruptedException {
        // Build the HTTP GET request, including the Bearer token in the headers
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))  // Add the base URL with the specific API path
                .header("Authorization", "Bearer " + token)  // Pass the token in the "Authorization" header
                .GET()  // Specify the GET method
                .build();

        // Send the request and handle the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 403){
            throw new AuthenticationException("Session has expired. Please log again.");
        }

        // Check if the response status is 200 (OK) and return the response body
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            // Throw an error if the request was unsuccessful
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }
    }

    public static void sendDeleteRequestWithToken(String path, String token)
            throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // If the response status code is 403, throw the custom AuthenticationException
        if (response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }

    }


}
