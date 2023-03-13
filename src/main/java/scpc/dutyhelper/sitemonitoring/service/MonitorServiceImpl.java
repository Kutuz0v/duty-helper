package scpc.dutyhelper.sitemonitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scpc.dutyhelper.sitemonitoring.model.Monitor;
import scpc.dutyhelper.sitemonitoring.repository.MonitorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final MonitorRepository repository;

    @Override
    public Monitor create(Monitor monitor) {
        monitor.setId(0L);
        return repository.save(monitor);
    }

    @Override
    public Monitor get(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Monitor> getAll() {
        return repository.findAll();
    }

    @Override
    public Monitor update(Long id, Monitor monitor) {
        if (get(id) == null) {
            return null;
        }
        monitor.setId(id);
        return repository.save(monitor);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
