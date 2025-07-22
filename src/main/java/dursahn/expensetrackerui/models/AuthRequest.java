package dursahn.expensetrackerui.models;

import lombok.Data;

@Data
public class AuthRequest {
    private String fullName; //required only for sign up
    private String username;
    private String password;
}
