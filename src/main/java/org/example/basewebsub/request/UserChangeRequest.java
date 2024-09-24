package org.example.basewebsub.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserChangeRequest {

    private String username;

    private String password;

    private String repassword;

}
