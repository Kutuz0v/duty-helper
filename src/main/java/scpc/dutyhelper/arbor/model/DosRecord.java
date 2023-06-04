package scpc.dutyhelper.arbor.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DosRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long arborAlertId;
    private String direction;
    private LocalDateTime startTime;
    private Boolean ongoing;
    private LocalDateTime stopTime;
    private Long maxImpactBps;
    private String ip;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DosRecord record)) return false;
        return Objects.equals(arborAlertId, record.arborAlertId)
                && Objects.equals(direction, record.direction)
                && Objects.equals(startTime, record.startTime)
                && Objects.equals(ongoing, record.ongoing)
                && Objects.equals(stopTime, record.stopTime)
                && Objects.equals(maxImpactBps, record.maxImpactBps)
                && Objects.equals(ip, record.ip)
                && Objects.equals(name, record.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arborAlertId, direction, startTime, ongoing, stopTime, maxImpactBps, ip, name);
    }
}
