package dursahn.expensetrackerui.models;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String message;
}
