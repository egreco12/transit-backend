package com.spezico.transit.service;

import com.spezico.transit.domain.Arrival;
import com.spezico.transit.oba.OBAArrivalsResponse;
import com.spezico.transit.oba.OBAArrivalsResponse.*;
import com.spezico.transit.oba.OBAClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ArrivalsServiceTest {

    @Test
    void getArrivalsForStop_mapsResponseCorrectly() {
        OBAClient mockClient = Mockito.mock(OBAClient.class);

        long now = System.currentTimeMillis();

        ArrivalAndDeparture ad = new ArrivalAndDeparture(
                "ROUTE_1",
                "TRIP_1",
                now + 5 * 60_000,     // scheduledArrivalTime
                now + 4 * 60_000,     // predictedArrivalTime
                0L,
                0L,
                1,
                true,
                "STOP_1",
                "VEH_1",
                "20250101",
                "Headsign from AD"
        );

        Route route = new Route(
                "ROUTE_1",
                "10",
                "Downtown Express"
        );

        Trip trip = new Trip(
                "TRIP_1",
                "ROUTE_1",
                "To Downtown",
                "SERVICE_1"
        );

        Entry entry = new Entry(List.of(ad));
        References refs = new References(
                List.of(route),
                List.of(trip),
                List.of(),
                List.of()
        );
        OBAArrivalsResponse.Data data = new OBAArrivalsResponse.Data(entry, refs);
        OBAArrivalsResponse obaResponse = new OBAArrivalsResponse(
                200,
                now,
                data,
                "OK",
                2
        );

        when(mockClient.getArrivalsForStop("STOP_1"))
                .thenReturn(Mono.just(obaResponse));

        ArrivalsService service = new ArrivalsService(mockClient);

        // Act
        var flux = service.getArrivalsForStop("STOP_1");

        StepVerifier.create(flux)
                .assertNext(arrival -> {
                    assertThat(arrival.routeId()).isEqualTo("ROUTE_1");
                    assertThat(arrival.routeShortName()).isEqualTo("10");
                    assertThat(arrival.headSign()).isEqualTo("To Downtown"); // from Trip
                    assertThat(arrival.predicted()).isTrue();
                    assertThat(arrival.etaSeconds()).isBetween(3 * 60, 5 * 60);
                })
                .verifyComplete();
    }
}
