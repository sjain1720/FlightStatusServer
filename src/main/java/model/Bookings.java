package model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Bookings")
@IdClass(BookingId.class)
public class Bookings {

    @Id
    @ManyToOne
    @JoinColumn(name = "flightId", referencedColumnName = "flightId")
    private Flights flight;

    @Id
    @ManyToOne
    @JoinColumn(name = "emailId", referencedColumnName = "emailId")
    private Users user;

    // Getters and Setters
}
