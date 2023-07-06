package com.example.flightapp;

import com.example.flightapp.model.Cargo;
import com.example.flightapp.model.Flight;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightDataRepository {
    private List<Flight> flights;

    private Map<Integer, Cargo> cargoData;

    public FlightDataRepository() throws IOException {
        // Load flight data from JSON and initialize the flights list
        // Load flight data from JSON file
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(OffsetDateTime.class, new CustomOffsetDateTimeDeserializer());
        objectMapper.registerModule(customModule);

        // Read flight data from flight-data.json
        flights = objectMapper.readValue(
                getClass().getResourceAsStream("/flight-data.json"),
                new TypeReference<List<Flight>>() {
                }
        );

        // Read cargo data from cargo-data.json
        cargoData = objectMapper.readValue(
                getClass().getResourceAsStream("/cargo-data.json"),
                new TypeReference<List<Cargo>>() {}
        ).stream().collect(Collectors.toMap(Cargo::getFlightId, cargo -> cargo));
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public Cargo getCargoForFlight(int flightId) {
        return cargoData.getOrDefault(flightId, null);
    }
}
