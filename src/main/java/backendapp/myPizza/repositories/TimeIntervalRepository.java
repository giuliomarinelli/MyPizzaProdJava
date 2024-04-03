package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.TimeInterval;
import backendapp.myPizza.Models.entities.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TimeIntervalRepository extends JpaRepository<TimeInterval, UUID> {
}
