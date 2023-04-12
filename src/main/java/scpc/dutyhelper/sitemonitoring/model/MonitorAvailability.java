package scpc.dutyhelper.sitemonitoring.model;


import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

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
    @Cascade(SAVE_UPDATE)
    @ManyToOne()
    @JoinColumn(
            name = "monitor_id",
            referencedColumnName = "id"
    )
    @JsonIncludeProperties("id")
    private Monitor monitor;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private State state;
    private Date checkedAt;

}
