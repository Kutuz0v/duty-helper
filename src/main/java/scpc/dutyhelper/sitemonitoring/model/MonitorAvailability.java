package scpc.dutyhelper.sitemonitoring.model;


import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MonitorAvailability {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne()
    @JoinColumn(
            name = "monitor_id",
            referencedColumnName = "id"
    )
    @JsonIncludeProperties("id")
    @JsonProperty(access = WRITE_ONLY)
    private Monitor monitor;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private State state;
    private LocalDateTime startPeriod;
    private LocalDateTime endPeriod;

    @Override
    public String toString() {
        return "id: " + id + ", " +
                monitor.getFriendlyName() + ", " +
                state + ", " +
                "start: " + startPeriod + ", " +
                "end: " + endPeriod;
    }

}
