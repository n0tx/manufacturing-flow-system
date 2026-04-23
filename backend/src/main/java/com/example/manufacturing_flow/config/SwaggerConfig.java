package com.example.manufacturing_flow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Manufacturing Flow System API")
                        .version("1.0")
                        .description("API Documentation for Manufacturing Flow System MVP\n\n" +
                                "**Demo Instructions:**\n" +
                                "1. Run `POST /api/auth/seed` first to generate dummy users.\n" +
                                "2. Login via `POST /api/auth/login` using these credentials:\n" +
                                "   - **admin** / **admin123** (Role: ADMIN)\n" +
                                "   - **gudang** / **gudang123** (Role: GUDANG)\n" +
                                "   - **produksi** / **produksi123** (Role: PRODUKSI)\n" +
                                "   - **finance** / **finance123** (Role: FINANCE)\n" +
                                "3. Copy the returned JWT Token.\n" +
                                "4. Click the **Authorize** button at the top right and paste your token.\n"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
