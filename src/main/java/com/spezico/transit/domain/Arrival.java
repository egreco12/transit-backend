package com.spezico.transit.domain;

public record Arrival(
  String routeId,
  String routeShortName,
  String headSign,
  int etaSeconds,
  long scheduledTimeEpochMs,
  boolean predicted
) {};
