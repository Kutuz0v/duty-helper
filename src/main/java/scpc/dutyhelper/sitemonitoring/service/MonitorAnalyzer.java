package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.telegram.service.TelegramService;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitorAnalyzer {
    private final TelegramService telegramService;
    private final MonitorService monitorService;

    public void analyzeMonitor(Monitor updatedMonitor) {
        Monitor currentMonitor = monitorService.get(updatedMonitor.getId());
        if (currentMonitor == null || updatedMonitor.equals(currentMonitor)) return;
        switch (updatedMonitor.getState()) {
            case UP -> recognizeUp(currentMonitor);
            case DOWN -> recognizeDown(currentMonitor);
            case PAUSED -> recognizePaused(currentMonitor);
        }
        monitorService.update(updatedMonitor.getId(), currentMonitor);
    }

    private void recognizePaused(Monitor monitor) {
        monitor.setCheckedAt(new Date());
        monitor.setState(State.PAUSED);
        String message = String.format("Monitor %s is PAUSED", getMonitorNameLink(monitor));
        telegramService.sendMessageForAll(message);
        log.warn(message);
    }

    private void recognizeDown(Monitor monitor) {
        monitor.setCheckedAt(new Date());
        monitor.setState(State.DOWN);
        String message = String.format("Monitor %s is DOWN", getMonitorNameLink(monitor));
        telegramService.sendMessageForAll(message);
        log.warn(message);
    }

    private void recognizeUp(Monitor monitor) {
        Date now = new Date();
        String timeDifference = getTimeDifference(
                Optional.ofNullable(monitor.getCheckedAt()).orElse(now),
                now);
        monitor.setCheckedAt(now);
        String monitorNameLink = getMonitorNameLink(monitor);
        String message = monitor.getState() == State.DOWN ?
                String.format("Monitor %s is UP after %s", monitorNameLink, timeDifference) :
                String.format("Monitor %s is UP", monitorNameLink);
        monitor.setState(State.UP);
        telegramService.sendMessageForAll(message);
        log.warn(message);
    }

    private String getMonitorNameLink(Monitor monitor) {
        return String.format("<a href=\"%s\">%s</a>", monitor.getUrl(), monitor.getFriendlyName());
    }

    private static String getTimeDifference(Date start_date,
                                            Date end_date) {

        long difference_In_Time
                = end_date.getTime() - start_date.getTime();

        long difference_In_Seconds
                = TimeUnit.MILLISECONDS
                .toSeconds(difference_In_Time)
                % 60;

        long difference_In_Minutes
                = TimeUnit
                .MILLISECONDS
                .toMinutes(difference_In_Time)
                % 60;

        long difference_In_Hours
                = TimeUnit
                .MILLISECONDS
                .toHours(difference_In_Time)
                % 24;

        long difference_In_Days
                = TimeUnit
                .MILLISECONDS
                .toDays(difference_In_Time)
                % 365;

        long difference_In_Years
                = TimeUnit
                .MILLISECONDS
                .toDays(difference_In_Time)
                / 365L;

        return (difference_In_Years != 0 ? difference_In_Years + " years, " : "") +
                (difference_In_Days != 0 ? difference_In_Days + " days, " : "") +
                (difference_In_Hours != 0 ? difference_In_Hours + " hours, " : "") +
                (difference_In_Minutes != 0 ? difference_In_Minutes + " minutes, " : "") +
                (difference_In_Seconds != 0 ? difference_In_Seconds + " seconds, " : "");
    }

}
