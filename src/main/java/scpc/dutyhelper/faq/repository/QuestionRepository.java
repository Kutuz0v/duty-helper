package scpc.dutyhelper.faq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scpc.dutyhelper.faq.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
