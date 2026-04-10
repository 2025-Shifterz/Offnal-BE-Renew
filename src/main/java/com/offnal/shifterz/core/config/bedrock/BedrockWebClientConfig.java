package com.offnal.shifterz.global.config.bedrock;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(BedrockProperties.class)
public class BedrockWebClientConfig {

	@Bean
	public WebClient bedrockWebClient(BedrockProperties props) {

		HttpClient httpClient = HttpClient.create()
			.responseTimeout(Duration.ofMillis(props.timeout().responseMs()));

		ExchangeStrategies strategies = ExchangeStrategies.builder()
			.codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
			.build();

		// ✅ 추가: WebClient URI 인코딩 방지
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.exchangeStrategies(strategies)
			.uriBuilderFactory(factory) // ✅ 추가
			.build();
	}
}

