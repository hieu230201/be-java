package org.example.basewebsub.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * chịu trách nhiệm cho việc base configuration cho security của cả ứng dụng
 *
 * @author hieunt
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class OAuth2GlobalMethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return getOAuth2MethodSecurityExpressionHandler();
    }

    @Bean
    public OAuth2MethodSecurityExpressionHandler getOAuth2MethodSecurityExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }
}



