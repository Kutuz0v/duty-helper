package scpc.dutyhelper.arbor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scpc.dutyhelper.arbor.model.DosRecord;

import java.util.Optional;

@Repository
public interface DosRepository extends JpaRepository<DosRecord, Long> {
    Optional<DosRecord> findByArborAlertId(Long id);
}
