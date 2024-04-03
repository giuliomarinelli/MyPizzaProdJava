package backendapp.myPizza.Models.resDTO;

import backendapp.myPizza.Models.entities.TimeInterval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeIntervalsRes {
    private List<TimeInterval> timeIntervals;
}
