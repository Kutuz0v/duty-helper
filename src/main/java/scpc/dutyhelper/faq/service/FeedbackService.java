package scpc.dutyhelper.faq.service;

import scpc.dutyhelper.faq.model.Feedback;

import java.util.List;

public interface FeedbackService {
    List<Feedback> getAll();

    Feedback create(Feedback feedback);

    void delete(Long id);
}
