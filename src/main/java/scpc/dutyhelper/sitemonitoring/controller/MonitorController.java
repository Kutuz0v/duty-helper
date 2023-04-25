package scpc.dutyhelper.sitemonitoring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.model.State;
import scpc.dutyhelper.sitemonitoring.payload.MonitorDto;
import scpc.dutyhelper.sitemonitoring.service.MonitorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class MonitorController {
    private final MonitorService monitorService;

    @GetMapping
    public List<MonitorDto> getAll() {
        return monitorService.getAll().stream().map(MonitorDto::new).toList();
    }

    @GetMapping("/{id}")
    public MonitorDto get(@PathVariable Long id) {
        return new MonitorDto(monitorService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MODERATOR')")
    public Monitor create(@RequestBody Monitor monitor) {
        if (monitor.getState() != State.PAUSED)
            monitor.setState(State.WAITING);
        return monitorService.create(monitor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public MonitorDto update(@PathVariable Long id, @RequestBody Monitor monitor) {
        log.warn("Update monitor (controller): {}", monitor);
        monitorService.update(id, monitor);
        return get(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public void delete(@PathVariable Long id) {
        monitorService.delete(id);
    }
}
