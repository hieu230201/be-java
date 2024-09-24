package org.example.basewebsub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi publicApi(@Value("menu-service") String apiDocs) {
        return GroupedOpenApi.builder()
                .group(apiDocs) // /v3/api-docs/api-service
                .packagesToScan("org.example.basewebsub.controller.menu")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi2(@Value("auth-service") String apiDocs) {
        return GroupedOpenApi.builder()
                .group(apiDocs) // /v3/api-docs/api-service
                .packagesToScan("org.example.basewebsub.controller.auth")
                .build();
    }

    @Bean
    public OpenAPI openAPI(
            @Value("API Service") String title,
            @Value("1.0.0") String version,
            @Value("http://150.95.108.202:443/basewebsub/") String serverUrl) {
        final String securitySchemeName = "basicAuth";
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl)))
                .info(new Info().title(title)
                        .description("API documents")
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName).type(SecurityScheme.Type.HTTP).scheme("basic")));
    }
}