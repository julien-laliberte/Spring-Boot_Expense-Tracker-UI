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

public class SignUpController {
    public MFXTextField fullNameField;
    public MFXTextField usernameField;
    public MFXPasswordField passwordField;
    public MFXPasswordField passwordRepeat;
    public MFXButton submitButton;

    public void handleSignUp(ActionEvent actionEvent) {
        AuthRequest request = new AuthRequest();
        request.setFullName(fullNameField.getText());
        request.setUsername(usernameField.getText());
        request.setPassword(passwordField.getText());

        Stage stage = (Stage) fullNameField.getScene().getWindow();
        AuthService.signUp(request, stage);
    }

    public void handleLogin(MouseEvent mouseEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/dursahn/expensetrackerui/views/LoginScreen.fxml"));
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            Scene loginScene = new Scene(loader.load());
            loginScene.getStylesheets().addFirst(getClass()
                    .getResource("/dursahn/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(loginScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
