package scpc.dutyhelper.sitemonitoring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.sitemonitoring.service.MonitorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class MonitorController {
    private final MonitorService service;

    @GetMapping
    public List<Monitor> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Monitor get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MODERATOR')")
    public Monitor create(@RequestBody Monitor monitor) {
        if (monitor.getState() != State.PAUSED)
            monitor.setState(State.WAITING);
        return service.create(monitor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public Monitor update(@PathVariable Long id, @RequestBody Monitor monitor) {
        log.warn("Update monitor (controller): {}", monitor);
        return service.update(id, monitor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
