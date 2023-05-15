package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.telegram.service.TelegramService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitorAnalyzer {
    private final TelegramService telegramService;
    private final MonitorService monitorService;
    private final MonitorAvailabilityService monitorAvailabilityService;
    private final Notifier notifier;

    /**
     * <Id, DownTimes>
     **/
    private final Map<Long, Integer> falsePositivesMonitors = new ConcurrentHashMap<>();

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
        notifier.notifyUp(monitor);
        monitor.setState(State.UP);
        monitor.setStateFrom(LocalDateTime.now());
        log.info("Monitor OK: " + monitor);
    }
    private void recognizeDown(Monitor monitor) {
        monitor.setState(State.DOWN);
        monitor.setStateFrom(LocalDateTime.now());
        notifier.placeOrderToNotify(monitor);
        log.warn("Monitor unavailable: " + monitor);


    }

    private void recognizePaused(Monitor monitor) {
        // TODO Check to refactor (delete false positives + notifications to Notifier)
        notifier.clear(monitor);
        falsePositivesMonitors.remove(monitor.getId());
        monitor.setState(State.PAUSED);
        monitor.setStateFrom(LocalDateTime.now());
        String message = String.format("️⚫Моніторинг ресурсу %s призупинено", getMonitorNameLink(monitor));
        telegramService.sendMessageForAll(message);
        log.warn(message);
    }

    private boolean isFalsePositivesDown(Monitor monitor) {
        Integer downTimes = falsePositivesMonitors.getOrDefault(monitor.getId(), 0);
        downTimes++;
        falsePositivesMonitors.put(monitor.getId(), downTimes);
        int FALSE_POSITIVE_DETECTION_LIMIT = 3;
        return downTimes < FALSE_POSITIVE_DETECTION_LIMIT;
    }

    private String getMonitorNameLink(Monitor monitor) {
        return String.format("<a href=\"%s\">%s</a>", monitor.getUrl(), monitor.getFriendlyName());
    }

}
