package org.example.basewebsub.controller.login;

import org.example.basewebsub.auth.beans.JWTTokenProvider;
import org.example.basewebsub.auth.models.UserPrincipal;
import org.example.basewebsub.config.security.CustomAuthenticationProviderRemote;
import org.example.basewebsub.exception.CustomServiceBusinessException;
import org.example.basewebsub.model.auth.LoginRequest;
import org.example.basewebsub.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    public JWTTokenProvider jwtTokenProvider;

    @Autowired
    private CustomAuthenticationProviderRemote customAuthenticationProvider;

    @Value("${jwt.access.validity}")
    private long jwtAccessValidity;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Tạo đối tượng Authentication từ yêu cầu
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername() + "#" + loginRequest.getSuperUser(),
                    loginRequest.getPassword()
            );

            // Xác thực người dùng
            Authentication authResult = customAuthenticationProvider.authenticate(authentication);

            if (authResult == null || !(authResult.getPrincipal() instanceof UserPrincipal)) {
                return ResponseEntity.badRequest().body("Invalid credentials");
            }

            UserPrincipal userPrincipal = (UserPrincipal) authResult.getPrincipal();
            String accessToken = (String) authResult.getCredentials();

            // Tạo đối tượng LoginResponse
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccess_token(accessToken);
            loginResponse.setToken_type("bearer");
            loginResponse.setAccess_token(jwtTokenProvider.generateToken(userPrincipal));
            loginResponse.setExpires_in(jwtAccessValidity);
            loginResponse.setToken_type("bearer");
            loginResponse.setAuthorizes(userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            return ResponseEntity.ok(loginResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        } catch (CustomServiceBusinessException e) {
            throw new RuntimeException(e);
        }
    }

}
