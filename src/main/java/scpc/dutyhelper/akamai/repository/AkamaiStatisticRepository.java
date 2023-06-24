package scpc.dutyhelper.akamai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scpc.dutyhelper.akamai.model.AkamaiStatistic;

public interface AkamaiStatisticRepository extends JpaRepository<AkamaiStatistic, Long> {
}
