package scpc.dutyhelper.faq.service;

import scpc.dutyhelper.faq.model.Question;

import java.util.List;

public interface QuestionService {
    Question create(Question question);

    Question get(Long id);

    List<Question> getAll();

    Question update(Long id, Question question);

    void delete(Long id);
}
