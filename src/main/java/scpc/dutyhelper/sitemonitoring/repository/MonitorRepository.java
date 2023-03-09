package scpc.dutyhelper.sitemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scpc.dutyhelper.sitemonitoring.model.Monitor;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {
}
