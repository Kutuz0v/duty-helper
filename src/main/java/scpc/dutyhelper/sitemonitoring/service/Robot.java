package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;

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
        } catch (Exception e) {
            log.error("{}: {}", monitor.getFriendlyName(), e.getMessage());
            monitor.setState(State.DOWN);
        }

        if (responseEntity != null) {
            monitor.setState(responseEntity.getStatusCode().value() == 200 ? State.UP : State.DOWN);
        }

        monitorAnalyzer.analyzeMonitor(monitor);
    }


}

