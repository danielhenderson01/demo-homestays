package com.dah.rs.health;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

  @GetMapping()
  public ResponseEntity<?> checkHealth() {
    return ResponseEntity.ok(Map.of("message", "healthy"));
  }

}
