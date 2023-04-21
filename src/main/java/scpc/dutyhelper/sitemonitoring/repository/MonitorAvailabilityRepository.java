package scpc.dutyhelper.sitemonitoring.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.MonitorAvailability;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MonitorAvailabilityRepository extends JpaRepository<MonitorAvailability, Long> {

    MonitorAvailability findTopByMonitorAndStartPeriodLessThanEqualOrderByStartPeriodDesc(Monitor monitor, LocalDateTime end);

    List<MonitorAvailability> findByMonitorAndStartPeriodBetweenOrEndPeriodBetweenOrderByStartPeriodAsc(
            Monitor monitor, LocalDateTime from, LocalDateTime to, LocalDateTime sameFrom, LocalDateTime sameTo
    );

    @Query("SELECT m FROM MonitorAvailability m where m.monitor = ?1 and (m.endPeriod between ?2 and ?3 or m.endPeriod = null)")
    List<MonitorAvailability> findByMonitorForPeriod(Monitor monitor, LocalDateTime from, LocalDateTime to, Sort sort);
}
