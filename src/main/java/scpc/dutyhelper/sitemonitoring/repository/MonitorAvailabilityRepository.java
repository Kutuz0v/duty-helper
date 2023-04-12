package scpc.dutyhelper.sitemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scpc.dutyhelper.sitemonitoring.model.MonitorAvailability;

@Repository
public interface MonitorAvailabilityRepository extends JpaRepository<MonitorAvailability, Long> {
}
