package com.spezico.transit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oba")
public record OBAClientProperties(
  String baseUrl,
  String apiKey
) {}
