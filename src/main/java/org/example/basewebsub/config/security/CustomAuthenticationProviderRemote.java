package org.example.basewebsub.config.security;

import lombok.SneakyThrows;
import org.example.basewebsub.auth.beans.JWTTokenProvider;
import org.example.basewebsub.auth.enums.TokenType;
import org.example.basewebsub.auth.models.UserPrincipal;
import org.example.basewebsub.comon.ErrorCode;
import org.example.basewebsub.entity.RolesEntity;
import org.example.basewebsub.entity.UsersEntity;
import org.example.basewebsub.exception.CustomServiceBusinessException;
import org.example.basewebsub.repository.RolesRepo;
import org.example.basewebsub.repository.UsersRepo;
import org.example.basewebsub.repository.UsersRolesRepo;
import org.example.basewebsub.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProviderRemote implements AuthenticationProvider {
    @Autowired
    public JWTTokenProvider jwtTokenProvider;

    @Autowired
    private UsersRolesRepo usersRolesRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private RolesRepo rolesRepo;

    @Value("${jwt.access.validity}")
    private long jwtAccessValidity;

    @Value("${app.config.appName}")
    private String appName;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public CustomAuthenticationProviderRemote() {

    }

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        var part = authentication.getName().split("#");
        if (part.length == 1) {
            return null;
        }
        var username = part[0];
        var password = authentication.getCredentials().toString();
        Long userId = null;
        UsersEntity user = usersRepo.findByUsernameAndDeleteFlagIsFalse(username);
        if (user != null) {
            userId = user.getId();
        }
        if (userId == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomServiceBusinessException(ErrorCode.USER_NOT_FOUND);
        }
        List<String> authorizes = getRolesByUser(userId);
        LoginResponse loginResponse = new LoginResponse();

        UserPrincipal info = new UserPrincipal(appName, username, userId, null, authorizes,
                UUID.randomUUID().toString(),
                TokenType.ACCESS_TOKEN,
                jwtTokenProvider
                        .generateExpireAt(TokenType.ACCESS_TOKEN));
        loginResponse.setAccess_token(jwtTokenProvider.generateToken(info));
        SecurityContextHolder.getContext().setAuthentication(null);
        if (loginResponse.getAccess_token() != null) {
            return new UsernamePasswordAuthenticationToken(info, password, authorizes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }
        return null;
    }

    private List<String> getRolesByUser(Long userId) {
        List<String> latestRoles = new ArrayList();

        var rolesList = usersRolesRepo.findByUserId(userId);
        if (rolesList.isEmpty()) {
            return latestRoles;
        }

        for (var role : rolesList) {
            Optional<RolesEntity> roles = rolesRepo.findById(role.getRoleId());
            if (roles.isPresent()) {
                latestRoles.add(roles.get().getName());
            }
        }

        return latestRoles;
    }

    @Override
    public boolean supports(Class authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}


