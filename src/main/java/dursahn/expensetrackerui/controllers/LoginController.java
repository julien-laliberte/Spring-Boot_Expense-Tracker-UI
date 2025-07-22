package dursahn.expensetrackerui.controllers;

import dursahn.expensetrackerui.models.AuthRequest;
import dursahn.expensetrackerui.services.AuthService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public MFXTextField usernameField;
    public MFXPasswordField passwordField;
    public MFXButton submitButton;

    public void handleLogin(ActionEvent actionEvent) {
        AuthRequest request = new AuthRequest();
        request.setUsername(usernameField.getText());
        request.setPassword(passwordField.getText());

        Stage stage = (Stage) usernameField.getScene().getWindow();
        AuthService.login(request, stage);
    }

    public void handleCreateAccount(MouseEvent mouseEvent) {

        try{
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/dursahn/expensetrackerui/views/SignUpScreen.fxml"));
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene signUpScene = new Scene(loader.load());
            signUpScene.getStylesheets().addFirst(getClass()
                    .getResource("/dursahn/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(signUpScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
