package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.exception.NotFoundException;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.MonitorAvailability;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.sitemonitoring.repository.MonitorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final MonitorRepository repository;

    @Override
    public Monitor create(Monitor monitor) {
        monitor.setId(0L);
        return repository.save(monitor);
    }

    @Override
    public Monitor get(Long id) {
        Monitor monitor = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Monitor with id " + id + " not found"));
        clearAvailabilityToLastDay(monitor);
        return monitor;
    }

    @Override
    public List<Monitor> getAll() {
        List<Monitor> monitors = repository.findAll();
        monitors.forEach(this::clearAvailabilityToLastDay);
        return monitors;
    }

    @Override
    public List<Monitor> getUnavailable() {
        return repository.findAllByStateIs(State.DOWN).stream()
                .sorted(Comparator.comparing(Monitor::getStateFrom))
                .toList();
    }

    @Override
    public Monitor update(Long id, Monitor monitor) {
        Monitor old = get(id);
        old.setFriendlyName(monitor.getFriendlyName());
        old.setUrl(monitor.getUrl());
        if (monitor.getState() != null) old.setState(monitor.getState());
        if (monitor.getStateFrom() != null) old.setStateFrom(monitor.getStateFrom());
        return repository.save(old);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void clearAvailabilityToLastDay(Monitor monitor) {
        List<MonitorAvailability> availabilities = Objects.requireNonNullElse(monitor.getAvailabilities(), new ArrayList<>());
        if (availabilities == null) return;
        List<MonitorAvailability> lastDayAvailabilities = availabilities.stream()
                .filter(availability ->
                        (availability.getEndPeriod() == null ||
                                availability.getEndPeriod().isAfter(LocalDateTime.now().minusDays(1))))
                .sorted((o1, o2) -> {
                    if (o1.getEndPeriod() == null) o1.setEndPeriod(LocalDateTime.now());
                    if (o2.getEndPeriod() == null) o2.setEndPeriod(LocalDateTime.now());
                    return o1.getEndPeriod().compareTo(o2.getEndPeriod());
                })
                .collect(Collectors.toList());
        monitor.setAvailabilities(lastDayAvailabilities);
    }
}
