package org.example.basewebsub.auth.beans;


import org.example.basewebsub.auth.models.UserPrincipal;
import org.example.basewebsub.logging.LogManage;
import org.example.basewebsub.logging.bases.ILogManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class UserAuthProvider {
    protected static final ILogManage logger = LogManage.getLogManage(UserAuthProvider.class);

    @Autowired
    JWTTokenProvider jwtTokenProvider;


    public UserPrincipal getUserPrincipal() {
        UserPrincipal userPrincipal = new UserPrincipal();
        try {
            OAuth2Authentication authentication= (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            String tokenValue = ((OAuth2AuthenticationDetails)authentication.getDetails()).getTokenValue();
            userPrincipal = jwtTokenProvider.getUserPrincipalFromToken(tokenValue);

        } catch (Exception e) {
            //logger.error(new MessageLog().setMessage(String.format("get UserPrincipal error %s", e.toString())));
        }
        return userPrincipal;
    }

    public String getUsername(){
        UserPrincipal userPrincipal = getUserPrincipal();
        if (userPrincipal == null)
            return "";
        return userPrincipal.getUsername();
    }
    public Long getUserId() {
        UserPrincipal userPrincipal = getUserPrincipal();
        if (userPrincipal == null)
            return 0L;
        return userPrincipal.getUserId();
    }
}
