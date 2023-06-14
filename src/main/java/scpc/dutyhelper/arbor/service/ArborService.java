package scpc.dutyhelper.arbor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import scpc.dutyhelper.arbor.model.ArborAlert;
import scpc.dutyhelper.arbor.model.DosRecord;
import scpc.dutyhelper.arbor.repository.DosRepository;
import scpc.dutyhelper.telegram.service.TelegramService;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static scpc.dutyhelper.util.TimeUtil.calculateTimeDifference;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArborService {
    private final RestTemplate restTemplate;
    private final DosRepository repository;
    private final TelegramService telegramService;
    @Value("${arbor.host}")
    private String arborHost;
    @Value("${arbor.apiKey}")
    private String arborApiKey;

    @Scheduled(fixedRate = 15_000)
    public void checkDos() {
        List<DosRecord> records = new ArrayList<>();
        try {
            String arborUrl = "/arborws/alerts";
            records = Arrays.stream(Objects.requireNonNull(
                            restTemplate.getForEntity(
                                    arborHost + arborUrl +
                                            String.format("?api_key=%s&format=%s&limit=%s&filter=%s",
                                                    arborApiKey,
                                                    "json",
                                                    "15",
                                                    "DoS"),
                                    ArborAlert[].class
                            ).getBody()))
                    .filter(alert -> alert.getDirection().equals("Incoming"))
                    .map(ArborAlert::toDosRecord)
                    .sorted(Comparator.comparingLong(DosRecord::getArborAlertId))
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            log.error(e.getMessage());
            telegramService.sendMessage(440024209L, "Сервіс не може обробити відповідь Арбора!\n" + e);
        }

        analyzeRecords(records);
    }

    private void analyzeRecords(List<DosRecord> records) {
        records.forEach(record -> repository.findByArborAlertId(record.getArborAlertId()).ifPresentOrElse(
                value -> {
                    if (!record.equals(value)) {
                        StringBuilder message = new StringBuilder();
                        String unexpected = "Це неочікувана поведінка, повідомте адміністратора сервісу!\n";
                        if (!record.getDirection().equals(value.getDirection())) {
                            message.append(String.format("'direction' field was changed to %s\n", record.getDirection()));
                            message.append(unexpected);
                            value.setDirection(record.getDirection());
                        }

                        if (!record.getStartTime().equals(value.getStartTime())) {
                            message.append(String.format("'start' field was changed to %s\n", record.getStartTime()));
                            message.append(unexpected);
                            value.setStartTime(record.getStartTime());
                        }

                        if (record.getStopTime() != null && !record.getStopTime().equals(value.getStopTime()) &&
                                !record.getOngoing().equals(value.getOngoing())) {
                            value.setStopTime(record.getStopTime());
                            value.setOngoing(record.getOngoing());
                            value.setMaxImpactBps(record.getMaxImpactBps());
                        }

                        if (!record.getOngoing().equals(value.getOngoing())) {
                            message.append(String.format("'ongoing' field was changed to %s\n", record.getOngoing()));
                            message.append(unexpected);
                            value.setOngoing(record.getOngoing());
                        }

                        if (record.getStopTime() != null && !record.getStopTime().equals(value.getStopTime())) {
                            message.append(String.format("'stop' field was changed to %s", record.getStopTime()));
                            message.append(unexpected);
                            value.setStopTime(record.getStopTime());
                        }
                        if (!record.getMaxImpactBps().equals(value.getMaxImpactBps())) {
                            message.append("❕Підвищення пікового навантаження.\n");
                            value.setMaxImpactBps(record.getMaxImpactBps());
                        }
                        if (!record.getIp().equals(value.getIp())) {
                            message.append(String.format("'IP' field was changed to %s\n", record.getId()));
                            message.append(unexpected);
                            value.setId(record.getId());
                        }
                        if (!record.getName().equals(value.getName())) {
                            message.append(String.format("'name' field was changed to %s\n", record.getName()));
                            message.append(unexpected);
                            value.setName(record.getName());
                        }

                        repository.save(value);
                        message.append(dosToMessage(record));
                        telegramService.sendMessageForAll(message.toString());
                        log.info("UPDATE: " + message.toString().replaceAll("\n", ", "));
                    }
                },
                () -> {
                    DosRecord saved = repository.save(record);
                    String message = dosToMessage(saved);
                    telegramService.sendMessageForAll(message);
                    log.info("NEW: " + message.replaceAll("\n", ", "));
                }
        ));
    }

    private String dosToMessage(DosRecord record) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("d MMMM HH:mm", new Locale("uk", "UA"));
        String stopTime = null;
        if (record.getStopTime() != null && record.getStartTime().getDayOfYear() == record.getStopTime().getDayOfYear()) {
            stopTime = record.getStopTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        return (record.getOngoing() ? "‼️" : "✅") +
                String.format("Dos %s (%s)\n%s\n",
                        record.getName(),
                        record.getIp(),
                        stringifyImpact(record.getMaxImpactBps())) +
                String.format("%s - %s",
                        record.getStartTime().format(formatter),
                        record.getOngoing() ? "триває" :
                                Optional.ofNullable(stopTime).orElseGet(() -> record.getStopTime().format(formatter)) +
                                        " (" + calculateTimeDifference(record.getStartTime(), record.getStopTime()) + ")");
    }

    private String stringifyImpact(Long maxImpactBps) {
        double impact = maxImpactBps.doubleValue();
        String result = String.format("%.2f Bps", impact);
        String[] units = {"Bps", "Kbps", "Mbps", "Gbps"};
        for (String unit : units) {
            if (impact < 1000d) {
                result = String.format("%.1f %s", impact, unit);
                break;
            }
            impact = impact / 1000;
        }
        return result;
    }

}
