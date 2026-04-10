package com.offnal.shifterz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = "com.offnal.shifterz.core.config")
public class ShifterzApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShifterzApplication.class, args);
	}

}
