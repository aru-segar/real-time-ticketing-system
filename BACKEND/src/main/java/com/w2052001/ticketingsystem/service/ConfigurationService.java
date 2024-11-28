package com.w2052001.ticketingsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.w2052001.ticketingsystem.module.Configuration;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    @Value("${config.file.path}")
    private String configFilePath; // Path to the config file in application.properties
    private final ObjectMapper objectMapper;

    @Autowired
    // Constructor injection for ObjectMapper
    public ConfigurationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Method to load configuration from the JSON file
    public Configuration loadConfiguration() throws IOException {
        Path filePath = Paths.get(configFilePath);
        File configFile = filePath.toFile();

        if (!configFile.exists()) {
            // Log that the configuration file does not exist and return default configuration
            logger.info("Configuration file not found, loading default configuration.");
            return new Configuration();
        }

        // Read and deserialize the configuration into a Configuration object
        try {
            Configuration configuration = objectMapper.readValue(configFile, Configuration.class);
            configuration.validate();
            return configuration;
        } catch (IOException e) {
            logger.error("Error reading configuration file", e);
            throw new IOException("Failed to load configuration from the file.");
        }
    }

    // Method to save the entered configuration to the JSON file
    public void saveConfiguration(@Valid Configuration configuration) throws IOException {
        // Validate configuration before saving
        configuration.validate();
        Path filePath = Paths.get(configFilePath);

        try {
            // Write the configuration object to the file as JSON
            objectMapper.writeValue(filePath.toFile(), configuration);
        } catch (IOException e) {
            logger.error("Error saving configuration", e);
            throw new IOException("Failed to save configuration to the file.");
        }
    }
}
