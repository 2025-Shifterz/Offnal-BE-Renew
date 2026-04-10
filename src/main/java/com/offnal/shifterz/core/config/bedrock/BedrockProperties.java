package com.offnal.shifterz.global.config.bedrock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "bedrock")
public record BedrockProperties(
	@NotBlank String region,
	@NotBlank String modelId,
	@NotNull @Valid Timeout timeout
) {
	public record Timeout(
		@Positive int connectMs,
		@Positive int responseMs
	) {}
}
