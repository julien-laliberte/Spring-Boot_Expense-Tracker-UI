package dursahn.expensetrackerui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dursahn.expensetrackerui.models.Expense;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class ExpenseDataParser {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())  // Register the custom adapter
            .create();

    public static String serializeExpense(Object expense) {
        return gson.toJson(expense);
    }

    public static List<String> parseCategoryList(String jsonResponse) {
        Type categoryListType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(jsonResponse, categoryListType);
    }

    // Method to parse JSON response to a List of Expense objects
    public static List<Expense> parseExpenseList(String jsonResponse) {
        // Use TypeToken to handle the fact that we're parsing a generic List<Expense>
        Type expenseListType = new TypeToken<List<Expense>>() {}.getType();
        return gson.fromJson(jsonResponse, expenseListType);  // Convert the JSON string into a list of Expense objects
    }
}
