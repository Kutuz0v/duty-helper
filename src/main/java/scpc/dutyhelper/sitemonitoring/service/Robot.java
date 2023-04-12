package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class Robot {

    private final RestTemplate restTemplate;
    private final MonitorAnalyzer monitorAnalyzer;

    @Async
    public void checkStatus(Monitor monitor) {
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
            log.error("1, {}: {}", monitor.getFriendlyName(), message);
            monitor.setState(State.DOWN);
        }
        // відповідь є але з проблемами
        catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 403
                    && Objects.requireNonNull(Objects.requireNonNull(e.getResponseHeaders()).get("Server"))
                    .get(0).equalsIgnoreCase("cloudflare")
            ) {
                monitor.setState(State.UP);
            } else {
                monitor.setState(State.DOWN);
                String message = Objects.requireNonNull(e.getMessage()).length() > 1000 ? e.getMessage().substring(0, 1000) : e.getMessage();
                log.error("2, {}: {}", monitor.getFriendlyName(), message);
            }

        } catch (Exception e) {
            String message = Objects.requireNonNull(e.getMessage()).length() > 1000 ? e.getMessage().substring(0, 1000) : e.getMessage();
            log.error("3, {}: {}", monitor.getFriendlyName(), message);
            monitor.setState(State.DOWN);
        }

        if (responseEntity != null) {
            monitor.setState(responseEntity.getStatusCode().value() == 200 ? State.UP : State.DOWN);
        }

        monitor.setCheckedAt(new Date());
        monitorAnalyzer.analyzeMonitor(monitor);
    }


}

