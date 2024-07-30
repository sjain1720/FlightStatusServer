package repo;
import model.BookingId;
import model.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface BookingRepository extends JpaRepository<Bookings, BookingId> {
    List<Bookings> findByFlightId(Long flightId);
}
