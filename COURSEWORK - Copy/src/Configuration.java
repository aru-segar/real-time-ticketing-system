import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Configuration {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    // Method to configure the system
    public void startCLI(Scanner scanner) {
        System.out.println("""
                \n---------------------------------------------
                        TICKETING SYSTEM CONFIGURATION
                ---------------------------------------------
                1. Set New Configuration
                2. Load Existing Configuration
                ---------------------------------------------""");

        // Prompt for a valid choice
        int choice;
        while (true) {
            try {
                System.out.print("Choose an option: ");
                choice = Integer.parseInt(scanner.next().trim());
                if (choice == 1 || choice == 2) {
                    break;
                } else {
                    System.out.println("ERROR: Please enter either 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid input. Please enter a valid option.");
            }
        }

        if (choice == 1) {
            setConfiguration(scanner);
            showConfigurationSummary();
            saveConfiguration("config.json");
            System.out.println("\nSUCCESS: System configuration updated successfully.");
        } else {
            Configuration loadedConfig = loadConfiguration("config.json");
            if (loadedConfig != null) {
                this.totalTickets = loadedConfig.totalTickets;
                this.ticketReleaseRate = loadedConfig.ticketReleaseRate;
                this.customerRetrievalRate = loadedConfig.customerRetrievalRate;
                this.maxTicketCapacity = loadedConfig.maxTicketCapacity;
                showConfigurationSummary();
                System.out.println("\nINFO: Configuration successfully loaded and applied.");
            }
        }
    }

    // Method to set configuration with user input
    private void setConfiguration(Scanner scanner) {
        System.out.println("\nPlease provide the necessary details to configure the system.\n");
        totalTickets = promptForValidInput(scanner, "Enter the total number of tickets available", 1);
        maxTicketCapacity = promptForValidInput(scanner, "Enter the maximum ticket capacity", totalTickets);
        ticketReleaseRate = promptForValidInput(scanner, "Enter the ticket release rate", 1);
        customerRetrievalRate = promptForValidInput(scanner, "Enter the customer retrieval rate", 1);
    }

    // General method to prompt for a valid integer input
    private int promptForValidInput(Scanner scanner, String prompt, int minValue) {
        int value;
        while (true) {
            try {
                System.out.println(prompt + ": ");
                value = Integer.parseInt(scanner.next().trim());
                if (value <= 0) {
                    throw new IllegalArgumentException("The value must be a positive integer.");
                } else if (value < minValue) {
                    throw new IllegalArgumentException("The value must be at least " + minValue + ".");
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid input. Please enter a valid integer.");
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + e.getMessage() + " Please try again.");
            }
        }
        return value;
    }

    // Method to display the summary of the entered configuration
    public void showConfigurationSummary() {
        System.out.printf("""
                        \n---------------------------------------------
                                     CONFIGURATION SUMMARY
                        ---------------------------------------------
                        Total Tickets            : %d tickets
                        Ticket Release Rate      : %d tickets per time unit
                        Customer Retrieval Rate  : %d tickets per time unit
                        Max Ticket Capacity      : %d tickets
                        ---------------------------------------------
                        """,
                totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
    }

    // Method to save configuration to JSON
    public void saveConfiguration(String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to save configuration to JSON.");
            e.printStackTrace();
        }
    }

    // Method to load configuration from JSON
    public static Configuration loadConfiguration(String fileName) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(fileName)) {
            return gson.fromJson(reader, Configuration.class);
        } catch (IOException e) {
            System.out.println("ERROR: Failed to load configuration from JSON.");
            e.printStackTrace();
            return null;
        }
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
}

