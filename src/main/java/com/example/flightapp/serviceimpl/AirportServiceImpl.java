package com.example.flightapp.serviceimpl;

import com.example.flightapp.FlightDataRepository;
import com.example.flightapp.model.Baggage;
import com.example.flightapp.model.Cargo;
import com.example.flightapp.model.Flight;
import com.example.flightapp.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AirportServiceImpl implements AirportService {

    private List<Flight> flights;
    private FlightDataRepository flightDataRepository;

    @Autowired
    public AirportServiceImpl(FlightDataRepository flightDataRepository) {
        this.flightDataRepository = flightDataRepository;
        this.flights = flightDataRepository.getFlights();
    }

    public int countDepartingFlights(String iataCode, OffsetDateTime flightDate) {
        return (int) flights.stream()
                .filter(flight -> flight.getDepartureAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate()))
                .count();
    }

    public int countArrivingFlights(String iataCode, OffsetDateTime flightDate) {
        return (int) flights.stream()
                .filter(flight -> flight.getArrivalAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate()))
                .count();
    }

    public int calculateTotalArrivingBaggage(String iataCode, OffsetDateTime flightDate) {
        int totalBaggage = 0;
        for (Flight flight : flights) {
            if (flight.getArrivalAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate())) {
                Cargo cargo = flightDataRepository.getCargoForFlight(flight.getFlightId());
                if (cargo != null) {
                    for (Baggage baggage : cargo.getBaggage()) {
                        totalBaggage += baggage.getPieces();
                    }
                }
            }
        }
        return totalBaggage;
    }

    public int calculateTotalDepartingBaggage(String iataCode, OffsetDateTime flightDate) {
        int totalBaggage = 0;
        for (Flight flight : flights) {
            if (flight.getDepartureAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate())) {
                Cargo cargo = flightDataRepository.getCargoForFlight(flight.getFlightId());
                if (cargo != null) {
                    for (Baggage baggage : cargo.getBaggage()) {
                        totalBaggage += baggage.getPieces();
                    }
                }
            }
        }
        return totalBaggage;
    }
}
