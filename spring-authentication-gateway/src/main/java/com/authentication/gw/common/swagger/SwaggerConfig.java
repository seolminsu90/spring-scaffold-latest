package com.authentication.gw.common.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("Authorization", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("Bearer")
                    .bearerFormat("JWT")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("Authorization", Arrays.asList("read", "write")))
            .info(new Info()
                .title("Spring Authorization Gateway sample")
                .description("게이트웨이와 인증을 통합한 스캐폴트 프로젝트")
                .version("1.0.0"));
    }
}
