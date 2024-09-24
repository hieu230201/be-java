package org.example.basewebsub.model.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String superUser;
    private String password;
}
