package dursahn.expensetrackerui.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExpenseData {
    private ObservableList<Expense> expenseList;

    public ExpenseData() {
        expenseList = FXCollections.observableArrayList();
    }

    public ObservableList<Expense> getExpenses() {
        return expenseList;
    }

    public void addExpense(Expense expense) {
        expenseList.add(expense);
    }


}
