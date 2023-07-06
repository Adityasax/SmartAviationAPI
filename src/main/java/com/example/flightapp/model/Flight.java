package com.example.flightapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;

public class Flight {
    private int flightId;
    private int flightNumber;
    private String departureAirportIATACode;
    private String arrivalAirportIATACode;

    @JsonFormat(pattern="uuuu-MM-dd'T'HH:m:ssXXX", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime departureDate;
    public OffsetDateTime getDepartureDate() {
        return departureDate;
    }

    private List<Cargo> cargo;

    public void setDepartureDate(OffsetDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public List<Cargo> getCargo() {
        return cargo;
    }

    public void setCargo(List<Cargo>  cargo) {
        this.cargo = cargo;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureAirportIATACode() {
        return departureAirportIATACode;
    }

    public void setDepartureAirportIATACode(String departureAirportIATACode) {
        this.departureAirportIATACode = departureAirportIATACode;
    }

    public String getArrivalAirportIATACode() {
        return arrivalAirportIATACode;
    }

    public void setArrivalAirportIATACode(String arrivalAirportIATACode) {
        this.arrivalAirportIATACode = arrivalAirportIATACode;
    }

}
