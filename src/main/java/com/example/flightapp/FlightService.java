package com.example.flightapp;

import java.time.OffsetDateTime;
import java.util.List;

public interface FlightService {
    Flight getFlightByNumberAndDate(int flightNumber, OffsetDateTime flightDate);
    int calculateCargoWeight(Flight flight);
    int calculateBaggageWeight(Flight flight);
    FlightDetails calculateFlightDetails(Flight flight);
    // Add other methods as needed
}
