package dursahn.expensetrackerui.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Expense {
    private int id;
    private String expenseType;  // 0 for expense, 1 for income
    private LocalDate date;
    private double amount;
    private String category;
    private String account;
    private String note;
}
