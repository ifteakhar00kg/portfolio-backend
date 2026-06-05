package com.ifteakar.portfolio_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
@CrossOrigin(origins = "http://localhost:8080")
public class StatusController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<?> getSystemStatus() {
        boolean databaseUp = false;
        long startTime = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                databaseUp = true;
            }
        } catch (Exception e) {
            databaseUp = false;
        }

        long latency = System.currentTimeMillis() - startTime;
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptime = rb.getUptime() / 1000;

        return ResponseEntity.ok().body(Map.of(
                "status", databaseUp ? "OPERATIONAL" : "DEGRADED",
                "database", databaseUp ? "CONNECTED" : "DISCONNECTED",
                "uptimeSeconds", uptime,
                "latencyMs", latency
        ));
    }
}