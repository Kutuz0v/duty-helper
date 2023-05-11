package scpc.dutyhelper.sitemonitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;

import java.util.List;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    List<Monitor> findAllByStateIs(State state);
}
