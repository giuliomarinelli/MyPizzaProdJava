package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.Order;
import backendapp.myPizza.Models.entities.TimeInterval;
import backendapp.myPizza.Models.entities.WorkSession;
import backendapp.myPizza.Models.enums.OrderStatus;
import backendapp.myPizza.Models.reqDTO.StartSessionDTO;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.repositories.OrderRepository;
import backendapp.myPizza.repositories.TimeIntervalRepository;
import backendapp.myPizza.repositories.WorkSessionRepository;
import jakarta.mail.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class _SessionService {
    @Autowired
    TimeIntervalRepository timeIntervalRp;

    @Autowired
    private OrderRepository orderRp;

    @Autowired
    private WorkSessionRepository _sessionRp;


    public List<Long> getActiveSessionDeliveryTimes() {
        ArrayList<Long> list = new ArrayList<>(_sessionRp.getActiveSessionTimeIntervals().stream().map(TimeInterval::getStartsAt).toList());
        ArrayList<Long> list2 = (ArrayList<Long>) (_sessionRp.getActiveSessionTimeIntervals().stream().map(TimeInterval::getEndsAt).toList());
        list.add(list2.get(list2.size() - 1));
        return list;
    }

    public List<TimeInterval> getActiveSessionTimeIntervals() {
        return _sessionRp.getActiveSessionTimeIntervals();
    }

    public boolean isThereAnActiveSession() {
        return _sessionRp.findAll().stream().anyMatch(WorkSession::isActive);
    }

    public WorkSession getActiveSession() throws BadRequestException {
        return _sessionRp.findAll().stream().filter(WorkSession::isActive).findFirst().orElseThrow(
                () -> new BadRequestException("There isn't any active session")
        );
    }

    public WorkSession startNewSession(StartSessionDTO startSessionDTO) throws BadRequestException {
        _sessionRp.saveAll(_sessionRp.findAll().stream().peek(s -> {
            if (s.isActive()) s.setActive(false);
        }).toList());
        WorkSession session = new WorkSession(startSessionDTO.type(), startSessionDTO.openTime(), startSessionDTO.closeTime(),
                startSessionDTO.cookCount(), startSessionDTO.ridersCount());
        session.setActive(true);
        _sessionRp.save(session);
        List<TimeInterval> timeIntervals = TimeInterval.getTimeIntervals(startSessionDTO.openTime(), startSessionDTO.closeTime());
        timeIntervalRp.saveAll(timeIntervals);
        for (TimeInterval ti : timeIntervals) {
            session.getTimeIntervals().add(ti);
        }
        return _sessionRp.save(session);
    }

    public ConfirmRes closeActiveSession() throws BadRequestException {
        WorkSession session = getActiveSession();
        session.setActive(false);
        _sessionRp.save(session);
        return new ConfirmRes("Working session with id='" + session.getId() + "' successfully closed", HttpStatus.OK);
    }

    public ConfirmRes addOrderToActiveSession(UUID orderId, UUID timeIntervalId) throws BadRequestException {
        Order order = orderRp.findById(orderId).orElseThrow(
                () -> new BadRequestException("Order wid id =" + orderId + "' doesn't exist")
        );
        WorkSession session = getActiveSession();
        TimeInterval timeInterval = session.getTimeIntervals().stream().filter(ti -> ti.getId().equals(timeIntervalId))
                .findFirst().orElseThrow(
                        () -> new BadRequestException("Time interval with id='" + timeIntervalId + "' doesn't " +
                                "exist in order with id='" + orderId + "'")
                );
        timeInterval.getOrders().add(order);
        timeIntervalRp.save(timeInterval);
        order.setStatus(OrderStatus.ACCEPTED);
        _sessionRp.save(session);
        return new ConfirmRes("Order wid id ='" + orderId + "' correctly added to active working session", HttpStatus.OK);
    }


}