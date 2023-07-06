package com.example.flightapp;
import com.example.flightapp.model.AirportDetails;
import com.example.flightapp.model.Flight;
import com.example.flightapp.model.FlightDetails;
import com.example.flightapp.service.AirportService;
import com.example.flightapp.service.FlightService;
import com.example.flightapp.serviceimpl.FlightServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private List<Flight> flights;
    @Autowired
    private FlightService flightService;
    @Autowired
    private AirportService airportService;

    private ObjectMapper objectMapper;

    public FlightController(FlightServiceImpl flightService) {
        this.flightService = flightService;
    }

    private Flight findFlightById(int flightId) {
        return flights.stream()
                .filter(flight -> flight.getFlightId() == flightId)
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<FlightDetails> getFlightDetails(
            @PathVariable int flightNumber,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime flightDate) {
        Flight requestedFlight = flightService.getFlightByNumberAndDate(flightNumber, flightDate);
        if (requestedFlight != null) {
            FlightDetails flightDetails = flightService.calculateFlightDetails(requestedFlight);
            return ResponseEntity.ok(flightDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/airport/{iataCode}")
    public ResponseEntity<AirportDetails> getAirportDetails(
            @PathVariable String iataCode,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssxxx") OffsetDateTime flightDate) {

        int departingFlights = airportService.countDepartingFlights(iataCode, flightDate);
        int arrivingFlights = airportService.countArrivingFlights(iataCode, flightDate);
        int totalArrivingBaggage = airportService.calculateTotalArrivingBaggage(iataCode, flightDate);
        int totalDepartingBaggage = airportService.calculateTotalDepartingBaggage(iataCode, flightDate);

        AirportDetails airportDetails = new AirportDetails(departingFlights, arrivingFlights, totalArrivingBaggage, totalDepartingBaggage);
        return ResponseEntity.ok(airportDetails);
    }

}
