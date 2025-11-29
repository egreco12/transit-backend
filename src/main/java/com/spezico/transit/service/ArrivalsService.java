package com.spezico.transit.service;

import com.spezico.transit.oba.OBAArrivalsResponse;
import com.spezico.transit.oba.OBAArrivalsResponse.ArrivalAndDeparture;
import com.spezico.transit.oba.OBAArrivalsResponse.Route;
import com.spezico.transit.oba.OBAArrivalsResponse.Trip;
import com.spezico.transit.oba.OBAClient;
import com.spezico.transit.domain.Arrival;

import java.util.Optional;
import java.util.function.Function;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ArrivalsService {
  
private final OBAClient obaClient;

public ArrivalsService(OBAClient client) {
  this.obaClient = client;
}

public Flux<Arrival> getArrivalsForStop(String stopId) {
  return obaClient.getArrivalsForStop(stopId).flatMapMany(this::mapToDomainArrivalsFlux);
}

private Flux<Arrival> mapToDomainArrivalsFlux(OBAArrivalsResponse obaResponse) {
  if (obaResponse == null
    || obaResponse.data() == null
    || obaResponse.data().entry() == null
    || obaResponse.data().entry().arrivalsAndDepartures() == null) {
      return Flux.empty();
    }

    Map<String, Route> routesById = Optional.ofNullable(obaResponse.data().references())
    .map(ref -> ref.routes())
    .orElse(List.of())
    .stream()
    .collect(Collectors.toMap(Route::id, Function.identity()));

    Map<String, Trip> tripsById = Optional.ofNullable(obaResponse.data().references())
                .map(ref -> ref.trips())
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(Trip::id, Function.identity()));

    long now = obaResponse.currentTime();

    return Flux.fromIterable(obaResponse.data().entry().arrivalsAndDepartures())
    .map(ad -> mapSingle(ad, routesById, tripsById, now))
    .filter(Objects::nonNull)
    .filter(a -> a.etaSeconds() > -60)
    .sort(Comparator.comparingLong(Arrival::arrivalTimeEpochMs));

}

private Arrival mapSingle(
            ArrivalAndDeparture ad,
            Map<String, Route> routesById,
            Map<String, Trip> tripsById,
            long nowEpochMs
    ) {
        if (ad == null) return null;

        long arrivalTime = ad.predicted()
                ? ad.predictedArrivalTime()
                : ad.scheduledArrivalTime();

        if (arrivalTime <= 0L) {
            arrivalTime = ad.scheduledArrivalTime();
        }
        if (arrivalTime <= 0L) {
            return null;
        }

        int etaSeconds = (int) ((arrivalTime - nowEpochMs) / 1000);

        Route route = routesById.get(ad.routeId());
        Trip trip = tripsById.get(ad.tripId());

        String routeShortName = route != null ? route.shortName() : null;

        String headsign =
                (trip != null && trip.tripHeadsign() != null && !trip.tripHeadsign().isBlank())
                        ? trip.tripHeadsign()
                        : (ad.headsign() != null && !ad.headsign().isBlank())
                        ? ad.headsign()
                        : (route != null ? route.longName() : "Unknown");

        return new Arrival(
                ad.routeId(),
                routeShortName,
                headsign,
                etaSeconds,
                ad.scheduledArrivalTime(),
                arrivalTime,
                ad.predicted()
        );
    }

}
