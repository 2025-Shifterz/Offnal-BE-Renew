package com.offnal.shifterz.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Profile;

@Profile("!prod")
@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "JWT";

	@Value("${swagger.server-url}")
	private String swaggerServerUrl;

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("Offnal API 명세서")
			.version("v2")
			.description("Offnal 교대근무 루틴 관리 서비스 API 명세서입니다.");

		// JWT 인증 방식 정의
		SecurityScheme securityScheme = new SecurityScheme()
			.name("Authorization")
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");

		// 인증 적용
		SecurityRequirement securityRequirement = new SecurityRequirement()
			.addList(SECURITY_SCHEME_NAME);

		return new OpenAPI()
			.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme))
			.addSecurityItem(securityRequirement)
			.addServersItem(new Server().url(swaggerServerUrl).description("API Server"))
			.info(info);
	}
}