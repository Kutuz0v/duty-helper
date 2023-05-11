package scpc.dutyhelper.sitemonitoring.service;


import scpc.dutyhelper.sitemonitoring.model.Monitor;

import java.util.List;

public interface MonitorService {
    Monitor create(Monitor monitor);

    Monitor get(Long id);

    List<Monitor> getAll();

    List<Monitor> getUnavailable();

    Monitor update(Long id, Monitor monitor);

    void delete(Long id);
}
