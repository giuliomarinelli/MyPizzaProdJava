package backendapp.myPizza.Models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "time_intervals")
public class TimeInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "timeInterval")
    List<Order> orders = new ArrayList<>();

    private long startsAt;

    private long endsAt;
//
//    @JsonIgnore
//    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "timeIntervals")
//    private List<WorkSession> workSessions = new ArrayList<>();


    private TimeInterval(long startsAt, long endsAt) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public static List<TimeInterval> getTimeIntervals(long startAllAt, long endAllAt) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        LocalDateTime startTime =
                Instant.ofEpochMilli(startAllAt).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endTime =
                Instant.ofEpochMilli(endAllAt).atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println("start " + startTime + "end " + endTime);
        startTime = startTime.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(15 * (startTime.getMinute() / 15));
        endTime = endTime.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(15 * (endTime.getMinute() / 15));
        System.out.println("start " + startTime + "end " + endTime);
        while (!startTime.isAfter(endTime)) {

            TimeInterval timeInterval = new TimeInterval(
                    startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    startTime.plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            );
            startTime = startTime.plusMinutes(15);
            System.out.println(startTime);
            timeIntervals.add(timeInterval);
        }
        return timeIntervals;

    }
}
