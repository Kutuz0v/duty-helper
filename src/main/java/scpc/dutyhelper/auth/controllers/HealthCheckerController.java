package scpc.dutyhelper.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthCheckerController {
    @GetMapping
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("HealthCheck - ok");
    }
}
