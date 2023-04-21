package scpc.dutyhelper.sitemonitoring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.MonitorAvailability;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.sitemonitoring.repository.MonitorAvailabilityRepository;
import scpc.dutyhelper.sitemonitoring.service.MonitorAvailabilityService;
import scpc.dutyhelper.sitemonitoring.service.MonitorService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class MonitorController {
    private final MonitorService monitorService;
    private final MonitorAvailabilityService monitorAvailabilityService;
    private final MonitorAvailabilityRepository repository;

    @GetMapping
    public List<Monitor> getAll() {
        return monitorService.getAll();
    }

    @GetMapping("/{id}")
    public Monitor get(@PathVariable Long id) {
        return monitorService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MODERATOR')")
    public Monitor create(@RequestBody Monitor monitor) {
        if (monitor.getState() != State.PAUSED)
            monitor.setState(State.WAITING);
        return monitorService.create(monitor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public Monitor update(@PathVariable Long id, @RequestBody Monitor monitor) {
        log.warn("Update monitor (controller): {}", monitor);
        return monitorService.update(id, monitor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public void delete(@PathVariable Long id) {
        monitorService.delete(id);
    }

    @GetMapping("/{id}/availability")
    public List<List<Object>> getAvailability(@PathVariable Long id) {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();
        List<MonitorAvailability> availabilityRecords = monitorAvailabilityService.getAvailability(id, from, to);
        return prepareAvailabilityRecords(availabilityRecords, from, to);
    }

    private List<List<Object>> prepareAvailabilityRecords(
            List<MonitorAvailability> availabilityRecords,
            LocalDateTime from,
            LocalDateTime to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<List<Object>> result = new ArrayList<>();

        availabilityRecords.forEach(record -> {
            if (record.getStartPeriod().isBefore(from)) record.setStartPeriod(from);
            if (record.getEndPeriod() == null || record.getEndPeriod().isAfter(to)) record.setEndPeriod(to);

            result.add(List.of(
                    record.getStartPeriod().format(formatter),
                    record.getState(),
                    record.getEndPeriod().format(formatter)
            ));
        });
        return result;
    }
}
