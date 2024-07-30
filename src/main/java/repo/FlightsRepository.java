package repo;
import model.Flights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface FlightsRepository extends JpaRepository<Flights, Long> {
}

