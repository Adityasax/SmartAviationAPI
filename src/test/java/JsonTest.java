import com.example.flightapp.Flight;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JsonTest {
    public static void main(String[] args) {
        // Schema for flight data
        String schema = "[\n" +
                "  '{{repeat(5)}}',\n" +
                "   {\n" +
                "     \"flightId\": \"{{index()}}\",\n" +
                "     \"flightNumber\": \"{{integer(1000, 9999)}}\",\n" +
                "     \"departureAirportIATACode\": \"{{random('SEA','YYZ','YYT','ANC','LAX')}}\",\n" +
                "     \"arrivalAirportIATACode\": \"{{random('MIT','LEW','GDN','KRK','PPX')}}\",\n" +
                "     \"departureDate\": \"{{date(new Date(2014, 0, 1), new Date(), 'YYYY-MM-dd'T'hh:mm:ssZ')}}\"\n" +
                "   }\n" +
                "]";

        // Generate flight data based on the schema
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Flight> flightData = objectMapper.readValue(schema, new TypeReference<List<Flight>>() {});
            // Use the generated flight data in your code
            // ...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
