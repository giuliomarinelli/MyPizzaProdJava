package backendapp.myPizza.repositories;

import backendapp.myPizza.Models.entities.TimeInterval;
import backendapp.myPizza.Models.entities.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface WorkSessionRepository extends JpaRepository<WorkSession, UUID> {
    @Query("SELECT s.timeIntervals FROM WorkSession s WHERE s.active = true")
    public List<TimeInterval> getActiveSessionTimeIntervals();
}
