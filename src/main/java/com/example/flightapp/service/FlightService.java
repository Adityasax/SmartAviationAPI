package com.example.flightapp.service;

import com.example.flightapp.model.Flight;
import com.example.flightapp.model.FlightDetails;

import java.time.OffsetDateTime;

public interface FlightService {
    Flight getFlightByNumberAndDate(int flightNumber, OffsetDateTime flightDate);
    int calculateCargoWeight(Flight flight);
    int calculateBaggageWeight(Flight flight);
    FlightDetails calculateFlightDetails(Flight flight);
    //Add other methods as needed
}
