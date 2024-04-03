package backendapp.myPizza.Models.entities;


import backendapp.myPizza.Models.enums.WorkSessionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "work_sessions")
@NoArgsConstructor
public class WorkSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "work_sessions_time_intervals",
            joinColumns = @JoinColumn(name = "work_session_id"),
            inverseJoinColumns = @JoinColumn(name = "time_interval_id")
    )
    private List<TimeInterval> timeIntervals = new ArrayList<>();


    private boolean active;

    @Enumerated(EnumType.STRING)
    private WorkSessionType type;

    private long openTime;

    private long closeTime;

    private int cookCount;

    private int ridersCount;

    private int maxAdvicedOrdersPerTimeInterval;

    public WorkSession(WorkSessionType type, long openTime, long closeTime, int cookCount, int ridersCount) {
        this.type = type;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.cookCount = cookCount;
        this.ridersCount = ridersCount;
        maxAdvicedOrdersPerTimeInterval = cookCount * ridersCount;
    }
}
