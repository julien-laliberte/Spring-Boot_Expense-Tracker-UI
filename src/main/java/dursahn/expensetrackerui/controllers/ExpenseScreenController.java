package dursahn.expensetrackerui.controllers;

import dursahn.expensetrackerui.models.Expense;
import dursahn.expensetrackerui.utils.ExpenseDataParser;
import dursahn.expensetrackerui.utils.HttpClientUtil;
import dursahn.expensetrackerui.utils.JwtStorageUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.fxml.FXML;

import javafx.stage.Stage;
import lombok.Setter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class ExpenseScreenController {
    @FXML
    public MFXComboBox expenseTypeDropdown;
    @FXML
    public DatePicker datePicker;
    @FXML
    public MFXTextField amountField;
    @FXML
    public MFXComboBox categoryDropdown;
    @FXML
    public MFXComboBox accountDropdown;
    @FXML
    public MFXTextField noteField;
    @FXML
    public MFXButton submitButton;

    @Setter
    private MainScreenController mainScreenController;
    private Integer expenseId = null;  // For editing an expense
    private boolean isEditMode = false;  // Flag to check if editing

    @FXML
    public void initialize() {
        // Set default values for ComboBoxes
        expenseTypeDropdown.getItems().addAll("Expense", "Income");
        categoryDropdown.getItems().addAll(Arrays.asList("Food", "Transport", "Travel", "Household", "Health",
                "Social life", "Gift", "Apparel", "Education", "Beauty", "Other"));
        accountDropdown.getItems().addAll(Arrays.asList("Bank", "Cash", "Card"));

        // Set default date to today if adding an expense
        if (!isEditMode) {
            datePicker.setValue(LocalDate.now());
        }
        datePicker.getEditor().setDisable(true);
        datePicker.getEditor().setOpacity(1);

    }

    // Method to initialize the screen for editing an expense
    public void initEditMode(int id, String expenseType, LocalDate date, double amount, String category, String account, String note) {
        this.expenseId = id;  // Store the ID of the expense being edited
        this.isEditMode = true;

        // Delay the setValue call until the UI thread has completed its rendering
        Platform.runLater(() -> {
            // Set the fields to the values of the expense being edited
            expenseTypeDropdown.setValue(expenseType.equals(0) ? "Expense" : "Income");
            datePicker.setValue(date);
            amountField.setText(String.valueOf(amount));
            categoryDropdown.setValue(category);
            accountDropdown.setValue(account);
            noteField.setText(note);
        });
    }

    // Method to handle form submission
    @FXML
    private void handleSubmit() {

        if (!validateForm()) {
            return;  // If validation fails, stop the form submission
        }

        String expenseType = expenseTypeDropdown.getValue().equals("Expense") ? "0" : "1";
        LocalDate date = datePicker.getValue();
        double amount = Double.parseDouble(amountField.getText());
        String category = categoryDropdown.getValue().toString();
        String account = accountDropdown.getValue().toString();
        String note = noteField.getText();

        // Prepare JSON data
        String jsonBody = ExpenseDataParser.serializeExpense(
                new Expense(0,
                        expenseType,
                        date,
                        amount,
                        category,
                        account,
                        note
                ));

        // Get the JWT token from storage
        String token = JwtStorageUtil.getToken();

        if (isEditMode) {
            System.out.println("Editing expense: ID=" + expenseId + ", Type=" + expenseType);

            String path = "/expenses/" + expenseId;

            // Make a PUT request to the API to update the expense
            try {
                HttpClientUtil.sendPutRequestWithToken(path, token, jsonBody);

                // After successful submission, refresh the main screen's expenses using the MainScreenController
                if (mainScreenController != null) {
                    System.out.println("Refreshing");
                    mainScreenController.refreshExpenses();  // Call the refreshExpenses method
                }

            } catch (AuthenticationException e) {
                handleAuthenticationFailure();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();

            } finally {
                // Close the form window after submission
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
            }
        } else {
            System.out.println("Adding new expense: Type=" + expenseType);
            // Make a POST request to the API
            String path = "/expenses";

            // Call the HttpClientUtil to fetch expenses by date
            try {
                HttpClientUtil.sendPostRequestWithToken(path, token, jsonBody);

                // After successful submission, refresh the main screen's expenses using the MainScreenController
                if (mainScreenController != null) {
                    System.out.println("Refreshing");
                    mainScreenController.refreshExpenses();  // Call the refreshExpenses method
                }

            } catch (AuthenticationException e) {
                handleAuthenticationFailure();
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                // Close the form window after submission
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    private void handleAuthenticationFailure() {
        // Show an alert dialog to the user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. Please log in again.");

        alert.showAndWait();
    }

    // Method to validate the form fields
    private boolean validateForm() {

        // Check if the expense type is selected
        if (expenseTypeDropdown.getValue() == null) {
            showErrorMessage("Please select an expense type.");
            return false;
        }

        // Validate the selected date
        LocalDate date;
        try {
            date = datePicker.getValue();
            if (date == null || date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.now().minusYears(1))) {
                showErrorMessage("Please select a valid date.");
                return false;
            }
        } catch (Exception e) {
            showErrorMessage("Invalid date selected.");
            return false;
        }

        // Check if the amount is a valid numeric value
        String amountText = amountField.getText();
        if (amountText == null || amountText.isEmpty()) {
            showErrorMessage("Amount cannot be empty.");
            return false;
        }
        try {
            Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showErrorMessage("Amount must be a numeric value.");
            return false;
        }

        // Check if a category is selected
        if (categoryDropdown.getValue() == null) {
            showErrorMessage("Please select a category.");
            return false;
        }

        // Check if an account is selected
        if (accountDropdown.getValue() == null) {
            showErrorMessage("Please select an account.");
            return false;
        }

        // Check if the note is not empty
        if (noteField.getText() == null || noteField.getText().isEmpty()) {
            showErrorMessage("Please enter a note.");
            return false;
        }

        // If all validations pass, return true
        return true;
    }

    // Helper method to display error messages
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
