package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class Robot {

    private final RestTemplate restTemplate;
    private final MonitorAnalyzer monitorAnalyzer;

    @Async
    public void checkStatus(Monitor monitor) {
        if (monitor.getState() == State.PAUSED) {
            monitorAnalyzer.analyzeMonitor(monitor);
            return;
        }

        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(
                    monitor.getUrl(),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    String.class
            );
        }
        // недоступний? ++
        catch (ResourceAccessException e) {
            String message = Objects.requireNonNull(e.getMessage()).length() > 1000 ? e.getMessage().substring(0, 1000) : e.getMessage();
            log.warn("1, {}: {}", monitor.getFriendlyName(), message);
            monitor.setState(State.DOWN);
        }
        // відповідь є але з проблемами
        catch (HttpClientErrorException e) {
            String responseServerHeader = "";
            HttpHeaders responseHeaders = e.getResponseHeaders();
            if (responseHeaders != null) {
                List<String> serverHeadersList = responseHeaders.get("Server");
                if (serverHeadersList != null && !serverHeadersList.isEmpty()) {
                    responseServerHeader = serverHeadersList.get(0);
                }
            }
            if (e.getRawStatusCode() == 403 && "cloudflare".equalsIgnoreCase(responseServerHeader)) {
                monitor.setState(State.UP);
            } else {
                monitor.setState(State.DOWN);
                String message = Objects.requireNonNull(e.getMessage()).length() > 1000 ? e.getMessage().substring(0, 1000) : e.getMessage();
                log.warn("2, {}: {}", monitor.getFriendlyName(), message);
            }

        } catch (Exception e) {
            String message = Objects.requireNonNull(e.getMessage()).length() > 1000 ? e.getMessage().substring(0, 1000) : e.getMessage();
            log.warn("3, {}: {}", monitor.getFriendlyName(), message);
            monitor.setState(State.DOWN);
        }

        if (responseEntity != null) {
            monitor.setState(responseEntity.getStatusCode().value() == 200 ? State.UP : State.DOWN);
        }

        monitor.setCheckedAt(new Date());
        monitorAnalyzer.analyzeMonitor(monitor);
    }


}

