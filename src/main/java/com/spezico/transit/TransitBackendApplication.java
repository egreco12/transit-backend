package com.spezico.transit;

import com.spezico.transit.config.OBAClientProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OBAClientProperties.class)
public class TransitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransitBackendApplication.class, args);
	}

}
