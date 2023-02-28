package scpc.dutyhelper.faq.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.faq.model.Feedback;
import scpc.dutyhelper.faq.repository.FeedbackRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository repository;

    @Override
    public List<Feedback> getAll() {
        return repository.findAll();
    }

    @Override
    public Feedback create(Feedback feedback) {
        return repository.save(feedback);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
