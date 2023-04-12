package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.MonitorAvailability;
import scpc.dutyhelper.sitemonitoring.repository.MonitorAvailabilityRepository;

@Service
@RequiredArgsConstructor
public class MonitorAvailabilityService {
    private final MonitorAvailabilityRepository repository;

    public MonitorAvailability saveAvailability(Monitor monitor) {
        if (monitor == null) return null;
        return repository.save(MonitorAvailability.builder()
                .id(0L)
                .monitor(monitor)
                .state(monitor.getState())
                .checkedAt(monitor.getCheckedAt())
                .build());
    }

}
