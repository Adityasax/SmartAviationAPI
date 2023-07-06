package com.example.flightapp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private List<Flight> flights;

    private FlightService flightService;

    private ObjectMapper objectMapper;
    public FlightController(ObjectMapper objectMapper, List<Flight> flights) {
        this.objectMapper = objectMapper;
        this.flights = flights;
    }

    public FlightController() throws IOException {

        // Load flight data from JSON file
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(OffsetDateTime.class, new CustomOffsetDateTimeDeserializer());
        objectMapper.registerModule(customModule);

        flights = objectMapper.readValue(
                getClass().getResourceAsStream("/flight-data.json"),
                new TypeReference<List<Flight>>() {}
        );

        // Read cargo data from cargo-data.json
        List<Cargo> cargoData = objectMapper.readValue(
                getClass().getResourceAsStream("/cargo-data.json"),
                new TypeReference<List<Cargo>>() {}
        );


        // Associate cargo data with flights
        for (Flight flight : flights) {
            for (Cargo cargo : cargoData) {
                if (cargo.getFlightId() == flight.getFlightId()) {
                    List<Cargo> flightCargoList = new ArrayList<>();
                    flightCargoList.add(cargo);
                    flight.setCargo(flightCargoList);
                    break;
                }
            }
        }


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
            FlightDetails flightDetails = calculateFlightDetails(requestedFlight);
            return ResponseEntity.ok(flightDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private FlightDetails calculateFlightDetails(Flight flight) {
        int cargoWeight = calculateCargoWeight(flight);
        int baggageWeight = calculateBaggageWeight(flight);
        int totalWeight = cargoWeight + baggageWeight;

        return new FlightDetails(cargoWeight, baggageWeight, totalWeight);
    }

    @GetMapping("/airport/{iataCode}")
    public ResponseEntity<AirportDetails> getAirportDetails(
            @PathVariable String iataCode,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssxxx") OffsetDateTime flightDate) {

        int departingFlights = countDepartingFlights(iataCode, flightDate);
        int arrivingFlights = countArrivingFlights(iataCode, flightDate);
        int totalArrivingBaggage = calculateTotalArrivingBaggage(iataCode, flightDate);
        int totalDepartingBaggage = calculateTotalDepartingBaggage(iataCode, flightDate);

        AirportDetails airportDetails = new AirportDetails(departingFlights, arrivingFlights, totalArrivingBaggage, totalDepartingBaggage);
        return ResponseEntity.ok(airportDetails);
    }

    private int countDepartingFlights(String iataCode, OffsetDateTime flightDate) {
        return (int) flights.stream()
                .filter(flight -> flight.getDepartureAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate()))
                .count();
    }

    private int countArrivingFlights(String iataCode, OffsetDateTime flightDate) {
        return (int) flights.stream()
                .filter(flight -> flight.getArrivalAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate()))
                .count();
    }


    private int calculateTotalArrivingBaggage(String iataCode, OffsetDateTime flightDate) {
        int totalBaggage = 0;
        for (Flight flight : flights) {
            if (flight.getArrivalAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate())) {
                for (Cargo cargo : flight.getCargo()) {
                    for (Baggage baggage : cargo.getBaggage()) {
                        totalBaggage += baggage.getPieces();
                    }
                }
            }
        }
        return totalBaggage;
    }

    private int calculateTotalDepartingBaggage(String iataCode, OffsetDateTime flightDate) {
        int totalBaggage = 0;
        for (Flight flight : flights) {
            if (flight.getDepartureAirportIATACode().equalsIgnoreCase(iataCode) && flight.getDepartureDate().toLocalDate().isEqual(flightDate.toLocalDate())) {
                for (Cargo cargo : flight.getCargo()) {
                    for (Baggage baggage : cargo.getBaggage()) {
                        totalBaggage += baggage.getPieces();
                    }
                }
            }
        }
        return totalBaggage;
    }


    private Flight findFlightByNumberAndDate(int flightNumber, OffsetDateTime flightDate) {
        return flights.stream()
                .filter(flight -> flight.getFlightNumber() == flightNumber && flight.getDepartureDate().isEqual(flightDate))
                .findFirst()
                .orElse(null);
    }


    private int calculateBaggageWeight(Flight flight) {
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

    private int calculateCargoWeight(Flight flight) {
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

    /*private static class FlightDetails {
        private int cargoWeight;
        private int baggageWeight;
        private int totalWeight;

        public FlightDetails(int cargoWeight, int baggageWeight, int totalWeight) {
            this.cargoWeight = cargoWeight;
            this.baggageWeight = baggageWeight;
            this.totalWeight = totalWeight;
        }

        public int getCargoWeight() {
            return cargoWeight;
        }

        public void setCargoWeight(int cargoWeight) {
            this.cargoWeight = cargoWeight;
        }

        public int getBaggageWeight() {
            return baggageWeight;
        }

        public void setBaggageWeight(int baggageWeight) {
            this.baggageWeight = baggageWeight;
        }

        public int getTotalWeight() {
            return totalWeight;
        }

        public void setTotalWeight(int totalWeight) {
            this.totalWeight = totalWeight;
        }

        // Getters and setters

        // toString method
    }*/
}
