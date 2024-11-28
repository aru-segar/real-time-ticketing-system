package com.w2052001.ticketingsystem.module;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Configuration {
    @NotNull
    private int totalTickets;

    @NotNull
    private int maxTicketCapacity;

    @NotNull
    private int ticketReleaseRate;

    @NotNull
    private int customerRetrievalRate;

    // Default constructor for deserialization
    public Configuration() {
        this.totalTickets = 100;
        this.maxTicketCapacity = 150;
        this.ticketReleaseRate = 30;
        this.customerRetrievalRate = 15;
    }

    // Method to validate the provided configuration
    public void validate() {
        // Ensure all configuration values are positive
        if (this.totalTickets <= 0 || this.maxTicketCapacity <= 0 || this.ticketReleaseRate <= 0 || this.customerRetrievalRate <= 0) {
            throw new IllegalArgumentException("Invalid configuration values detected. Please ensure all fields are positive.");
        }

        // Ensure the maximum ticket capacity is less than or equal to total tickets available
        if (this.maxTicketCapacity > this.totalTickets) {
            throw new IllegalArgumentException("The maximum ticket capacity cannot be greater than the total tickets available in the system.");
        }
    }

}
