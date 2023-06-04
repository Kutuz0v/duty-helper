package scpc.dutyhelper.arbor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArborAlert {
    private Long id;
    private String direction;
    private LocalDateTime start;
    private Boolean ongoing;
    private LocalDateTime stop;
    private ArborResource resource;
    @JsonProperty("max_impact_bps")
    private Long maxImpactBps;

    public DosRecord toDosRecord() {
        return DosRecord.builder()
                .arborAlertId(this.id)
                .name(this.resource.managedObjects[0].getName())
                .ip(this.resource.cidr)
                .direction(this.direction)
                .maxImpactBps(this.maxImpactBps)
                .startTime(this.start)
                .ongoing(this.ongoing)
                .stopTime(this.stop)
                .build();
    }

    @ToString
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArborResource {
        private String cidr;                        // IP
        private ManagedObject[] managedObjects;

        @ToString
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ManagedObject {
            private String name;                    // Name
        }
    }
}


