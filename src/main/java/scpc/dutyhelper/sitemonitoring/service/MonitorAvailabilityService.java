package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.MonitorAvailability;
import scpc.dutyhelper.sitemonitoring.repository.MonitorAvailabilityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MonitorAvailabilityService {
    private final MonitorAvailabilityRepository repository;
    private final MonitorService monitorService;
    private final Map<Long, MonitorAvailability> lastRecords = new ConcurrentHashMap<>();

    public List<MonitorAvailability> getAvailability(Long monitorId, LocalDateTime from, LocalDateTime to) {
        return repository.findByMonitorForPeriod(
                monitorService.get(monitorId),
                from,
                to,
                Sort.by("startPeriod")
        );
    }

    public void approveChangeState(Monitor monitor) {
        MonitorAvailability lastRecord = loadLastRecord(monitor);
        if (lastRecord.getState().equals(monitor.getState())) return;

        lastRecord.setEndPeriod(LocalDateTime.now());
        saveRecord(lastRecord);

        MonitorAvailability newRecord = createRecord(monitor);
        saveRecord(newRecord);
    }

    private MonitorAvailability loadLastRecord(Monitor monitor) {
        MonitorAvailability lastCachedRecord = lastRecords.get(monitor.getId());
        if (lastCachedRecord != null) return lastCachedRecord;

        MonitorAvailability recordFromDB = repository.findTopByMonitorAndStartPeriodLessThanEqualOrderByStartPeriodDesc(monitor, LocalDateTime.now());

        return saveRecord(recordFromDB == null ? createRecord(monitor) : recordFromDB);
//        return Objects.requireNonNullElseGet(recordFromDB, () -> saveRecord(createRecord(monitor)));
    }

    private MonitorAvailability saveRecord(MonitorAvailability record) {
        MonitorAvailability savedRecord = repository.save(record);
        lastRecords.put(savedRecord.getMonitor().getId(), savedRecord);
        return savedRecord;
    }

    private MonitorAvailability createRecord(Monitor monitor) {
        return MonitorAvailability.builder()
                .id(0L)
                .monitor(monitor)
                .state(monitor.getState())
                .startPeriod(LocalDateTime.now())
                .endPeriod(null)
                .build();
    }
}
