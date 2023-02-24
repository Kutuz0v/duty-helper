package scpc.dutyhelper.faq.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.faq.model.Question;
import scpc.dutyhelper.faq.repository.QuestionRepository;
import scpc.dutyhelper.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository repository;

    @Override
    public Question create(Question question) {
        question.setId(0L);
        return repository.save(question);
    }

    @Override
    public Question get(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Question with id " + id + " not found")
        );
    }

    @Override
    public List<Question> getAll() {
        return repository.findAll();
    }

    @Override
    public Question update(Long id, Question question) {
        get(id);
        question.setId(id);
        return repository.save(question);
    }

    @Override
    public void delete(Long id) {
        repository.delete(get(id));
    }
}
