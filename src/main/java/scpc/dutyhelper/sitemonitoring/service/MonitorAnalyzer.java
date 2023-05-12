package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.telegram.service.TelegramService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static scpc.dutyhelper.util.TimeUtil.getTimeDifference;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitorAnalyzer {
    private final TelegramService telegramService;
    private final MonitorService monitorService;
    private final MonitorAvailabilityService monitorAvailabilityService;

    /**
     * <Id, DownTimes>
     **/
    private final Map<Long, Integer> falsePositivesMonitors = new HashMap<>();

    public void analyzeMonitor(Monitor updatedMonitor) {
        Monitor currentMonitor = monitorService.get(updatedMonitor.getId());
        if (currentMonitor != null) monitorAvailabilityService.approveChangeState(updatedMonitor);

        if (currentMonitor == null ||
                updatedMonitor.equals(currentMonitor) ||
                State.PAUSED.equals(currentMonitor.getState())) return;

        switch (updatedMonitor.getState()) {
            case UP -> recognizeUp(currentMonitor);
            case DOWN -> recognizeDown(currentMonitor);
            case PAUSED -> recognizePaused(currentMonitor);
        }
        monitorService.update(updatedMonitor.getId(), currentMonitor);
    }

    private void recognizeUp(Monitor monitor) {
        falsePositivesMonitors.remove(monitor.getId());
        Date now = new Date();
        String timeDifference = getTimeDifference(
                Optional.ofNullable(monitor.getCheckedAt()).orElse(now),
                now);
        monitor.setCheckedAt(now);
        String monitorNameLink = getMonitorNameLink(monitor);
        String message = monitor.getState() == State.DOWN ?
                String.format("Ресурс %s доступний після %s", monitorNameLink, timeDifference) :
                String.format("Ресурс %s доступний", monitorNameLink);
        monitor.setState(State.UP);
        telegramService.sendMessageForAll(message);
        log.warn(message);
    }

    private void recognizeDown(Monitor monitor) {
        if (!isFalsePositivesDown(monitor)) {
            monitor.setCheckedAt(new Date());
            monitor.setState(State.DOWN);
            String message = String.format("Ресурс %s недоступний", getMonitorNameLink(monitor));
            telegramService.sendMessageForAll(message);
            log.warn(message);
        } else
            log.info(
                    String.format("Monitor %s have %d down times", monitor, falsePositivesMonitors.get(monitor.getId())));
    }

    private void recognizePaused(Monitor monitor) {
        falsePositivesMonitors.remove(monitor.getId());
        monitor.setCheckedAt(new Date());
        monitor.setState(State.PAUSED);
        String message = String.format("Моніторинг ресурсу %s призупинено", getMonitorNameLink(monitor));
        telegramService.sendMessageForAll(message);
        log.warn(message);
    }

    private boolean isFalsePositivesDown(Monitor monitor) {
        Integer downTimes = falsePositivesMonitors.getOrDefault(monitor.getId(), 0);
        downTimes++;
        falsePositivesMonitors.put(monitor.getId(), downTimes);
        return downTimes < 5;
    }

    private String getMonitorNameLink(Monitor monitor) {
        return String.format("<a href=\"%s\">%s</a>", monitor.getUrl(), monitor.getFriendlyName());
    }

}
