package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class Watcher {
    private final MonitorService monitorService;
    private final Robot robot;

    @Scheduled(fixedRate = 45_000)
    public void runMonitoringState() {
        List<Monitor> monitors = monitorService.getAll();

        monitors
                .forEach(robot::checkStatus);

    }


}
