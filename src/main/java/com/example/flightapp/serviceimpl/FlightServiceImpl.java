package com.example.flightapp.serviceimpl;

import com.example.flightapp.FlightDataRepository;
import com.example.flightapp.model.*;
import com.example.flightapp.service.FlightService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {
    private List<Flight> flights;
    private FlightDataRepository flightDataRepository;

    public FlightServiceImpl(FlightDataRepository flightDataRepository) {
        this.flightDataRepository = flightDataRepository;
        this.flights = flightDataRepository.getFlights();
        assignCargoDataToFlights();
    }

    private void assignCargoDataToFlights() {
        for (Flight flight : flights) {
            Cargo cargo = flightDataRepository.getCargoForFlight(flight.getFlightId());
            if (cargo != null) {
                List<Cargo> cargoList = new ArrayList<>();
                cargoList.add(cargo);
                flight.setCargo(cargoList);
            }
        }
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

    private int calculateCargoWeight(Cargo cargo) {
        int totalWeight = 0;
        for (Baggage baggage : cargo.getBaggage()) {
            totalWeight += convertWeightToKg(baggage.getWeight(), baggage.getWeightUnit()) * baggage.getPieces();
        }
        for (CargoItem cargoItem : cargo.getCargo()) {
            totalWeight += convertWeightToKg(cargoItem.getWeight(), cargoItem.getWeightUnit()) * cargoItem.getPieces();
        }
        return totalWeight;
    }

    private int convertWeightToKg(int weight, String weightUnit) {
        if (weightUnit.equalsIgnoreCase("lb")) {
            return (int) Math.round(weight * 0.453592); // Convert pounds to kilograms
        } else {
            return weight; // Already in kilograms
        }
    }

    @Override
    public int calculateBaggageWeight(Flight flight) {
        // Implement the logic to calculate the baggage weight
        if (flight.getCargo() != null) {
            int baggageWeight = 0;
            for (Cargo cargo : flight.getCargo()) {
                for (Baggage baggage : cargo.getBaggage()) {
                    baggageWeight += baggage.getWeight();
                }
            }
            return baggageWeight;
        } else {
            return 0;
        }
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
