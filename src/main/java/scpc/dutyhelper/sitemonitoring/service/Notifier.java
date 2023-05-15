package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.telegram.service.TelegramService;
import scpc.dutyhelper.util.TimeUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static scpc.dutyhelper.util.TimeUtil.getTimeDifference;

@Component
@RequiredArgsConstructor
@Slf4j
public class Notifier {
    private final MonitorService monitorService;
    private final TelegramService telegramService;
    private final Map<Monitor, LocalDateTime> monitorsToNotifyDown = new ConcurrentHashMap<>();
    private final long UNAVAILABLE_NOTIFICATION_MINUTES_DELAY = 10;

    @Scheduled(fixedRate = 15_000)
    public void notifyUnavailable() {
        monitorsToNotifyDown.keySet().forEach(monitor -> {
            if (monitorsToNotifyDown.get(monitor).isBefore(LocalDateTime.now().minusMinutes(UNAVAILABLE_NOTIFICATION_MINUTES_DELAY))) {
                notifyDown(monitor);
                monitorsToNotifyDown.remove(monitor);
            }
        });
    }

    public void placeOrderToNotify(Monitor monitor) {
        monitorsToNotifyDown.put(monitor, monitor.getStateFrom());
    }

    public void notifyUp(Monitor monitor) {
        monitorsToNotifyDown.remove(monitor);

        String message = String.format("\uD83D\uDFE2Ресурс %s доступний", getMonitorNameLink(monitor));
        if (monitor.getState() == State.DOWN) {
            message += String.format(
                    " після %s",
                    getTimeDifference(
                            Date.from(monitor.getStateFrom().atZone(ZoneId.systemDefault()).toInstant()),
                            new Date()));
            log.warn(message);
            if (!monitor.getStateFrom().isBefore(
                    LocalDateTime.now().minusMinutes(UNAVAILABLE_NOTIFICATION_MINUTES_DELAY))) return;
        }

        telegramService.sendMessageForAll(message);

    }

    private void notifyDown(Monitor monitor) {
        Monitor actualMonitor = monitorService.get(monitor.getId());
        if (actualMonitor == null || actualMonitor.getState() != State.DOWN) return;

        String unavailableTime = TimeUtil.getTimeDifference(
                Date.from(monitor.getStateFrom().atZone(ZoneId.systemDefault()).toInstant()),
                new Date()
        );
        String message = String.format("\uD83D\uDD34Ресурс %s недоступний %s", getMonitorNameLink(monitor), unavailableTime);
        telegramService.sendMessageForAll(message);
    }

    private String getMonitorNameLink(Monitor monitor) {
        return String.format("<a href=\"%s\">%s</a>", monitor.getUrl(), monitor.getFriendlyName());
    }

    public void clear(Monitor monitor) {
        monitorsToNotifyDown.remove(monitor);
    }
}
