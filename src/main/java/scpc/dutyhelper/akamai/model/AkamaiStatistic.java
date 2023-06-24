package scpc.dutyhelper.akamai.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AkamaiStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer hitSec;

    private LocalDateTime fromTime;

    private LocalDateTime toTime;

    private Integer count;

    private Integer size;

}
