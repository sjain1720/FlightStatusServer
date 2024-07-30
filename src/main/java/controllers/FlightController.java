package controllers;
import com.flightStatus.Server.FlightService;
import model.Flights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    public ResponseEntity<List<Flights>> fetchFlights(@RequestParam Map<String, String> searchParams) {
        List<Flights> flights = flightService.getFlights(searchParams);
        return ResponseEntity.ok(flights);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Flights> updateFlight(@PathVariable Long id, @RequestBody Flights updatedFlight) {
        Flights flight = flightService.updateFlight(id, updatedFlight);
        return ResponseEntity.ok(flight);
    }
}
