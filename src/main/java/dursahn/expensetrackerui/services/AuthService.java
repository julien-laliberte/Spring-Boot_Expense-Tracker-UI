package dursahn.expensetrackerui.services;

import com.google.gson.Gson;
import dursahn.expensetrackerui.models.AuthRequest;
import dursahn.expensetrackerui.models.AuthResponse;
import dursahn.expensetrackerui.utils.HttpClientUtil;
import dursahn.expensetrackerui.utils.JwtStorageUtil;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class AuthService {
    private static final String BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();

    public static void signUp(AuthRequest request, Stage stage) {
        new Thread(() -> {
                    try  {
                        String url = BASE_URL + "/signup";
                        String jsonBody = gson.toJson(request);
                        HttpResponse<String> response = HttpClientUtil
                                .sendPostRequest(url, jsonBody);
                        AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);

                        if("Success".equals(authResponse.getMessage())){
                            JwtStorageUtil.saveToken(authResponse.getToken());
                            Platform.runLater(() -> navigateToMainScreen(stage));
                        } else {
                            System.out.println(authResponse.getMessage());
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    public static void login(AuthRequest request, Stage stage) {
        new Thread(() -> {
            try  {
                String url = BASE_URL + "/login";
                String jsonBody = gson.toJson(request);
                HttpResponse<String> response = HttpClientUtil
                        .sendPostRequest(url, jsonBody);
                AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);

                if("Success".equals(authResponse.getMessage())){
                    JwtStorageUtil.saveToken(authResponse.getToken());
                    Platform.runLater(() -> navigateToMainScreen(stage));
                } else {
                    System.out.println(authResponse.getMessage());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        ).start();
    }

    private static void navigateToMainScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(AuthService.class.getResource("/dursahn/expensetrackerui/views/MainScreen.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(AuthService.class.getResource("/dursahn/expensetrackerui/css/main_screen.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not load the Main Screen.");
            alert.showAndWait();
        }
    }

}
