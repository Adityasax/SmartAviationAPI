package com.example.flightapp;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {
    private List<Flight> flights;

    public FlightServiceImpl(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public Flight getFlightByNumberAndDate(int flightNumber, OffsetDateTime flightDate) {
        // Implement the logic to find and return the flight
        return flights.stream()
                .filter(flight -> flight.getFlightNumber() == flightNumber && flight.getDepartureDate().isEqual(flightDate))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int calculateCargoWeight(Flight flight) {
        // Implement the logic to calculate the cargo weight
        if (flight.getCargo() != null) {
            int totalWeight = 0;
            for (Cargo cargo : flight.getCargo()) {
                totalWeight += calculateCargoWeight(cargo);
            }
            return totalWeight;
        } else {
            return 0;
        }
    }

    @Override
    public int calculateBaggageWeight(Flight flight) {
        // Implement the logic to calculate the baggage weight
    }

    @Override
    public FlightDetails calculateFlightDetails(Flight flight) {
        int cargoWeight = calculateCargoWeight(flight);
        int baggageWeight = calculateBaggageWeight(flight);
        int totalWeight = cargoWeight + baggageWeight;

        return new FlightDetails(cargoWeight, baggageWeight, totalWeight);
    }

    // Implement other methods of the FlightService interface as needed
}
