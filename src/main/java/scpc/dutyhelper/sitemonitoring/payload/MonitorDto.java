package scpc.dutyhelper.sitemonitoring.payload;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.MonitorAvailability;
import scpc.dutyhelper.sitemonitoring.model.State;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class MonitorDto {
    private final Long id;
    private final String friendlyName;
    private final String url;

    private final State state;
    private final Date checkedAt;
    private final List<List<Object>> availabilities;

    public MonitorDto(Monitor monitor) {
        this.id = monitor.getId();
        this.friendlyName = monitor.getFriendlyName();
        this.url = monitor.getUrl();
        this.state = monitor.getState();
        this.checkedAt = monitor.getCheckedAt();
        this.availabilities = prepareAvailabilityRecords(
                monitor.getAvailabilities(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now());
    }

    @NotNull
    public static List<List<Object>> prepareAvailabilityRecords(List<MonitorAvailability> availabilityRecords,
                                                                LocalDateTime availabilitiesFrom,
                                                                LocalDateTime availabilitiesTo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<List<Object>> result = new ArrayList<>();

        availabilityRecords.forEach(record -> {
            if (record.getStartPeriod().isBefore(availabilitiesFrom))
                record.setStartPeriod(availabilitiesFrom);
            if (record.getEndPeriod() == null || record.getEndPeriod().isAfter(availabilitiesTo))
                record.setEndPeriod(availabilitiesTo);

            result.add(List.of(
                    record.getStartPeriod().format(formatter),
                    record.getState(),
                    record.getEndPeriod().format(formatter)
            ));
        });
        return result;
    }
}
