package com.spezico.transit.oba;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OBAArrivalsResponse(
        int code,
        long currentTime,
        Data data,
        String text,
        int version
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            Entry entry,
            References references
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(
            List<ArrivalAndDeparture> arrivalsAndDepartures
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ArrivalAndDeparture(
            String routeId,
            String tripId,
            long scheduledArrivalTime,
            long predictedArrivalTime,
            long scheduledDepartureTime,
            long predictedDepartureTime,
            int stopSequence,
            boolean predicted,
            String stopId,
            String vehicleId,
            String serviceDate,
            // Additional OBA fields (ignored automatically)
            String headsign
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record References(
            List<Route> routes,
            List<Trip> trips,
            List<Stop> stops,
            List<Agency> agencies
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Route(
            String id,
            String shortName,
            String longName
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Trip(
            String id,
            String routeId,
            String tripHeadsign,
            String serviceId
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Stop(
            String id,
            String name,
            double lat,
            double lon
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Agency(
            String id,
            String name,
            String url
    ) {}
}
