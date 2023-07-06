package com.example.flightapp.service;

import java.time.OffsetDateTime;

public interface AirportService {
    int countDepartingFlights(String iataCode, OffsetDateTime flightDate);

    int countArrivingFlights(String iataCode, OffsetDateTime flightDate);

    int calculateTotalArrivingBaggage(String iataCode, OffsetDateTime flightDate);

    int calculateTotalDepartingBaggage(String iataCode, OffsetDateTime flightDate);
}
