package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class Watcher {

    private final MonitorService monitorService;
    private final Robot robot;


    @Scheduled(fixedRate = 30_000)
    public void runMonitoringState() {
        List<Monitor> monitors = monitorService.getAll();

        monitors.stream()
                .filter(monitor -> monitor.getState() != State.PAUSED)
                .forEach(robot::checkStatus);

    }


}
