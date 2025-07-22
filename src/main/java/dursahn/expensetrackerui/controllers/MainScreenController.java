package dursahn.expensetrackerui.controllers;

import dursahn.expensetrackerui.models.Expense;
import dursahn.expensetrackerui.models.ExpenseData;
import dursahn.expensetrackerui.utils.ExpenseDataParser;
import dursahn.expensetrackerui.utils.HttpClientUtil;
import dursahn.expensetrackerui.utils.JwtStorageUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MainScreenController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private MFXButton addExpenseButton;

    @FXML
    private MFXButton viewMonthlyStatsButton;

    @FXML
    private MFXButton logoutButton;

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, String> descriptionColumn;

    @FXML
    private TableColumn<Expense, Double> amountColumn;

    @FXML
    private TableColumn<Expense, LocalDate> dateColumn;

    @FXML
    private TableColumn<Expense, Void> editColumn;

    @FXML
    private TableColumn<Expense, Void> deleteColumn;

    private ExpenseData expenseData;

    @FXML
    public void initialize() {
        // Set today's date in the DatePicker
        LocalDate currentDate = LocalDate.now();
        datePicker.setValue(currentDate);

        // Set placeholder message when no data is available
        expenseTable.setPlaceholder(new Label("No expenses for this date"));

        // Enable automatic column resizing
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Bind columns to Expense model fields
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Add Edit and Delete buttons
        addEditButtonToTable();
        addDeleteButtonToTable();

        // Add all columns to the table
        expenseTable.getColumns().addAll(
                categoryColumn,
                descriptionColumn,
                amountColumn,
                dateColumn,
                editColumn,
                deleteColumn
        );

        // Fetch expenses for today's date
        fetchExpensesByDate(currentDate.toString());

        // Refresh expenses when a new date is selected
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fetchExpensesByDate(newValue.toString());
            }
        });
    }

    // Fetch expenses by date from the API
    private void fetchExpensesByDate(String date) {
        String formattedDate = date;  // Ensure the date is in yyyy-MM-dd format
        String token = JwtStorageUtil.getToken();

        if (token == null || token.isEmpty()) {
            System.out.println("No token found. User is not authenticated.");
            return;
        }

        String path = "/expenses/day/" + formattedDate;

        // Call the HttpClientUtil to fetch expenses by date
        try {
            String response = HttpClientUtil.sendGetRequestWithToken(path, token);

            // Parse the JSON response and convert it to a list of expenses
            List<Expense> expenses = ExpenseDataParser.parseExpenseList(response);

            // Clear the current table data
            expenseTable.getItems().clear();
            expenseTable.getItems().addAll(expenses);

        } catch (InterruptedException | IOException e) {
            if (e.getMessage().contains("404")) {
                expenseTable.getItems().clear();
                System.out.println("No expenses found for this date: " + formattedDate);
            } else {
                e.printStackTrace();
            }
        }
    }

    public void refreshExpenses() {
        // Get the current date selected in the DatePicker
        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate != null) {
            // Convert the date to a string and fetch expenses for the selected date
            fetchExpensesByDate(selectedDate.toString());
        }
    }

    private void handleAuthenticationFailure(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. Please log in again");
        alert.setOnHidden(evt -> handleLogout());
        alert.showAndWait();

    }

    // Adding Edit button in each row
    private void addEditButtonToTable() {
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactory = param -> new TableCell<Expense, Void>() {
            private final MFXButton btn = new MFXButton("Edit");

            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    System.out.println("Editing: " + expense.getNote());
                    if (expense != null) {
                        openExpenseScreenInEditMode(expense);
                    }
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };
        editColumn.setCellFactory(cellFactory);
    }

    private void openExpenseScreenInEditMode(Expense expense){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dursahn/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();

            ExpenseScreenController expenseScreenController = loader.getController();
            expenseScreenController.setMainScreenController(this);

            // Pass the reference of the main screen controller
            expenseScreenController.initEditMode(
                    expense.getId(),
                    expense.getExpenseType(),
                    expense.getDate(),
                    expense.getAmount(),
                    expense.getCategory(),
                    expense.getAccount(),
                    expense.getNote()
            );

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);

            // Load the CSS file for the screen
            scene.getStylesheets().add(getClass().getResource(
                    "/dursahn/expensetrackerui/css/expense_screen.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Edit Expense");
            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adding Delete button in each row
    private void addDeleteButtonToTable() {
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactory = param -> new TableCell<Expense, Void>() {
            private final MFXButton btn = new MFXButton("Delete");

            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    System.out.println("Deleting: " + expense.getNote());
                    if (expense != null) {
                        showDeleteConfirmation(expense);
                    }
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };
        deleteColumn.setCellFactory(cellFactory);
    }

    private void showDeleteConfirmation(Expense expense){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete confirmation");
        alert.setHeaderText("Are you sure you want to delete this expense?");
        alert.setContentText("Expense: " + expense.getNote());
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteExpense(expense);
            } else {
                System.out.println("Delete canceled.");
            }
        });
    }

    private void deleteExpense(Expense expense){
        String path = "/expenses/" + expense.getId();
        String token = JwtStorageUtil.getToken();

        try{
            HttpClientUtil.sendDeleteRequestWithToken(path, token);
            System.out.println("Deleted expense: " + expense.getNote());
            refreshExpenses();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Clear the JWT token from storage
        JwtStorageUtil.clearToken();

        // Navigate back to the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dursahn/expensetrackerui/views/LoginScreen.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene loginScene = new Scene(loader.load());

            // Load the CSS file
            loginScene.getStylesheets().add(getClass().getResource("/dursahn/expensetrackerui/css/style.css").toExternalForm());

            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddExpense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dursahn/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();

            ExpenseScreenController expenseScreenController = loader.getController();

            // Pass the reference of the main screen controller
            expenseScreenController.setMainScreenController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);

            // Load the CSS file for the screen
            scene.getStylesheets().add(getClass().getResource("/dursahn/expensetrackerui/css/expense_screen.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Add Expense");

            // Set the window to be non-resizable
            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewMonthlyStats() {
        try {
            // Load the statistics screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dursahn/expensetrackerui/views/StatisticsScreen.fxml"));
            VBox statisticsPane = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);  // Block interaction with the main screen
            Scene scene = new Scene(statisticsPane);

            // Load the CSS file for styling the statistics screen
            scene.getStylesheets().add(getClass().getResource("/dursahn/expensetrackerui/css/statistics_screen.css").toExternalForm());

            // Set the title and show the screen
            stage.setTitle("Monthly Statistics");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}