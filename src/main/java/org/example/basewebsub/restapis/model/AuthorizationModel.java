package org.example.basewebsub.restapis.model;

import lombok.Data;

@Data
public class AuthorizationModel {
    private Boolean isAuthenToken = true;
    private String token;
    private String user;
    private String password;
}