package dursahn.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.time.LocalDate;

public class StatisticController {
    @FXML
    private PieChart expensePieChart;

    @FXML
    private MFXComboBox<String> monthPicker;

    @FXML
    private MFXComboBox<Integer> yearPicker;

    @FXML
    private MFXButton backButton;

    @FXML
    public void initialize() {
        // Initialize the month picker with hardcoded months
        monthPicker.getItems().addAll("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");

        Platform.runLater(() -> {
            // Set the current month as the default value
            int currentMonth = LocalDate.now().getMonthValue();
            monthPicker.setValue(monthPicker.getItems().get(currentMonth - 1));

            // Initialize the year picker with a range of years (e.g., from 2020 to the current year)
            int currentYear = LocalDate.now().getYear();
            for (int year = 2020; year <= currentYear; year++) {
                yearPicker.getItems().add(year);
            }
            yearPicker.setValue(currentYear);  // Set the current year as default

            // Load the initial pie chart data (hardcoded for now)
            loadPieChartData();
        });

        // Handle the month picker change
        monthPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Reload the pie chart data when the month is changed (for now, same hardcoded data)
            loadPieChartData();
        });

        // Handle the year picker change
        yearPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadPieChartData();  // Reload pie chart data when year is changed
        });

        // Back button action
        backButton.setOnAction(event -> handleBackButton());
    }

    // Method to load hardcoded pie chart data
    private void loadPieChartData() {
        // Clear previous data
        expensePieChart.getData().clear();

        // Add hardcoded expenses by category
        PieChart.Data foodExpense = new PieChart.Data("Food", 300);
        PieChart.Data transportExpense = new PieChart.Data("Transport", 150);
        PieChart.Data healthExpense = new PieChart.Data("Health", 200);
        PieChart.Data shoppingExpense = new PieChart.Data("Shopping", 250);

        expensePieChart.getData().addAll(foodExpense, transportExpense, healthExpense, shoppingExpense);
    }

    // Method to handle the back button
    @FXML
    private void handleBackButton() {
        // Close the current stage (window)
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
