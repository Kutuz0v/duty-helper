package scpc.dutyhelper.sitemonitoring.model;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Monitor {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String friendlyName;
    @URL
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private State state;
    private Date checkedAt;
    @OneToMany(mappedBy = "monitor", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<MonitorAvailability> availabilities;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime stateFrom;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Monitor monitor = (Monitor) o;

        if (!id.equals(monitor.id)) return false;
        if (!Objects.equals(friendlyName, monitor.friendlyName))
            return false;
        if (!url.equals(monitor.url)) return false;
        return state == monitor.state;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
        result = 31 * result + url.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "id: " + id + ", " + friendlyName + ": " + state;
    }
}
