package com.yummy.beckend.config;

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
                        .title("Yummy Recipe AI Project API")
                        .description("야미(Yummy) 프로젝트의 API 명세서입니다.")
                        .version("v1.0.0"));
    }
}