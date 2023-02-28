package scpc.dutyhelper.faq.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import scpc.dutyhelper.faq.model.Feedback;
import scpc.dutyhelper.faq.service.FeedbackService;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER')")
public class FeedbackController {
    private final FeedbackService service;

    @GetMapping
    public List<Feedback> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Feedback create(@RequestBody Feedback feedback) {
        return service.create(feedback);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR') or hasAuthority('ADMINISTRATOR')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
