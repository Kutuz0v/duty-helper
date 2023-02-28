package scpc.dutyhelper.faq.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import scpc.dutyhelper.auth.model.UserDetailsImpl;
import scpc.dutyhelper.faq.model.Question;
import scpc.dutyhelper.faq.service.QuestionService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService service;

    @PostMapping
    @PreAuthorize("hasAuthority('MODERATOR')")
    public Question create(@RequestBody Question question) {
        log(question);
        return service.create(question);
    }

    @GetMapping("/{id}")
    public Question get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<Question> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public Question update(@PathVariable Long id, @RequestBody Question question) {
        log(question);
        return service.update(id, question);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public void delete(@PathVariable Long id) {
        log(id);
        service.delete(id);
    }

    private void log(Object o) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String callingMethodName = stackTraceElements[2].getMethodName();
        log.info("{} {} {}", principal.getEmail(), callingMethodName, o);
    }

}
