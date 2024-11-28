package com.w2052001.ticketingsystem.controller;

import com.w2052001.ticketingsystem.module.Configuration;
import com.w2052001.ticketingsystem.service.ConfigurationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ConfigurationController {
    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping("/configuration")
    public ResponseEntity<String> saveConfiguration(@Valid @RequestBody Configuration configuration) {
        try {
            configurationService.saveConfiguration(configuration);
            return ResponseEntity.ok("Configuration saved successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving configuration.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid configuration: " + e.getMessage());
        }
    }

    @GetMapping("/configuration")
    public ResponseEntity<?> getConfig() {
        try {
            Configuration configuration = configurationService.loadConfiguration();
            return ResponseEntity.ok(configuration);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error retrieving configuration: " + e.getMessage());
        }
    }
}
