package com.authentication.gw.common.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Spring Authorization Gateway sample")
                .description("게이트웨이와 인증을 통합한 스캐폴트 프로젝트")
                .version("1.0.0"));
    }
}
