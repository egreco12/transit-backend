package com.spezico.transit.controller;

import com.spezico.transit.domain.Arrival;
import com.spezico.transit.service.ArrivalsService;

import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stops")
public class ArrivalsController {
  private final ArrivalsService arrivalsService;

  public ArrivalsController(ArrivalsService arrivalsService) {
    this.arrivalsService = arrivalsService;
  }

  @GetMapping("/{stopId}/arrivals")
    public Flux<Arrival> getArrivals(@PathVariable String stopId) {
        return arrivalsService.getArrivalsForStop(stopId);
    }

}
