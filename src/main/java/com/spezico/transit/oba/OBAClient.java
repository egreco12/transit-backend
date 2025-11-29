package com.spezico.transit.oba;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.spezico.transit.config.OBAClientProperties;

import reactor.core.publisher.Mono;

@Component
public class OBAClient {

  private final WebClient webClient;
  private final OBAClientProperties obaProps;

public OBAClient(WebClient.Builder builder, OBAClientProperties props) {
  this.obaProps = props;
  this.webClient = builder.baseUrl(props.baseUrl()).build();
}

public Mono<OBAArrivalsResponse> getArrivalsForStop(String stopId) {
  return webClient.get()
  .uri(uri -> 
    uri.path("/arrivals-and-departures-for-stop/{id}.json")
    .queryParam("key", obaProps.apiKey())
    .build("stopId"))
    .retrieve()
    .bodyToMono(OBAArrivalsResponse.class);
}

}