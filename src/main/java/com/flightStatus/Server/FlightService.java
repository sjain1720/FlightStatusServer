package com.flightStatus.Server;

import model.Bookings;
import model.Flights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repo.BookingRepository;
import repo.FlightsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightService {

    @Autowired
    private FlightsRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EmailService emailService;

    // Method to get flights based on search parameters
    public List<Flights> getFlights(Map<String, String> searchParams) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Flights> query = cb.createQuery(Flights.class);
        Root<Flights> flight = query.from(Flights.class);

        List<Predicate> predicates = new ArrayList<>();

        searchParams.forEach((key, value) -> {
            if (value != null && !value.isEmpty()) {  // Avoid adding predicates for null or empty values
                switch (key) {
                    case "departureDate":
                        predicates.add(cb.equal(flight.get("departureDate"), LocalDate.parse(value)));
                        break;
                    case "flightNumber":
                        predicates.add(cb.equal(flight.get("flightNumber"), value));
                        break;
                    case "from":
                        predicates.add(cb.equal(flight.get("fromLocation"), value));
                        break;
                    case "to":
                        predicates.add(cb.equal(flight.get("toLocation"), value));
                        break;
                    case "flightId":
                        predicates.add(cb.equal(flight.get("flightId"), Long.parseLong(value)));  // Assuming flightId is Long
                        break;
                }
            }
        });
        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

    // Method to update a flight
    public Flights updateFlight(Long id, Flights updatedFlight) {
        Flights flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        // Update the flight details here if needed
        flight.setAddArrivalTime(updatedFlight.getAddArrivalTime());
        flight.setAddDepartureTime(updatedFlight.getAddDepartureTime());
        flight.setGateNumber(updatedFlight.getGateNumber());

        // Save the updated flight
        Flights updatedFlightEntity = flightRepository.save(flight);

        // Retrieve users who have booked this flight
        List<Bookings> bookings = bookingRepository.findByFlightId(id);
        List<String> userEmails = bookings.stream()
                .map(booking -> booking.getUser().getEmailId())
                .distinct()
                .collect(Collectors.toList());

        // Send email notifications
        sendEmailNotifications(userEmails, updatedFlightEntity);

        return updatedFlightEntity;
    }

    private void sendEmailNotifications(List<String> emails, Flights flight) {
        // Format for parsing and formatting date-time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Parse existing times
        LocalDateTime departureDateTime = LocalDateTime.parse(flight.getDepartureDate() + " " + flight.getDepartureTime(), formatter);
        LocalDateTime arrivalDateTime = LocalDateTime.parse(flight.getArrivalDate() + " " + flight.getArrivalTime(), formatter);

        // Add additional minutes
        departureDateTime = departureDateTime.plusMinutes(flight.getAddDepartureTime());
        arrivalDateTime = arrivalDateTime.plusMinutes(flight.getAddArrivalTime());

        String subject = "Flight Update Notification";
        String body = "Dear User,\n\n" +
                "Please be informed that the details of flight " + flight.getFlightId() + " have been updated.\n" +
                "New Departure Date: " + flight.getDepartureTime() + "\n" +
                "New Departure Time: " + flight.getDepartureTime() + "\n" +
                "New Arrival Date: " + flight.getArrivalTime() + "\n" +
                "New Arrival Time: " + flight.getArrivalTime() + "\n" +
                "Gate Number: " + flight.getGateNumber() + "\n\n" +
                "Thank you for choosing our service.\n\n" +
                "Best regards,\n" +
                "InterGlobe Aviation Ltd";

        for (String email : emails) {
            emailService.sendEmail(new String[]{email}, subject, body);
        }
    }
}
