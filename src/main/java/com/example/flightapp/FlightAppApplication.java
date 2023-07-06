package com.example.flightapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class FlightAppApplication {

    public static void main(String[] args) {

        SpringApplication.run(FlightAppApplication.class, args);
        /*String strDateTime = "2019-09-19T08:32:08-02:00";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:m:ssXXX");
        OffsetDateTime odt = OffsetDateTime.parse(strDateTime, dtf);
        System.out.println(odt);*/

    }
}
