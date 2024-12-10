import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SystemController {
    private static TicketPool ticketPool = null;
    private static final List<Thread> vendorThreads = new ArrayList<>();
    private static final List<Thread> customerThreads = new ArrayList<>();
    private static boolean systemConfigured = false;
    private static boolean systemRunning = false;

    //private static final Random random = new Random();
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Configuration config = new Configuration();

        System.out.println("\nWelcome to the Ticketing Management System!");
        System.out.println("Follow the instructions to configure and manage your ticketing system efficiently.");

        while (true) {
            System.out.println("""
                    \n-------------------------------------------
                          TICKETING MANAGEMENT SYSTEM MENU
                    -------------------------------------------
                     1. Configure the System
                     2. Start the System
                     3. Stop the System
                     4. Add Vendor
                     5. Remove Vendor
                     6. Add Customer
                     7. Remove Customer
                     8. View System Status
                     9. Exit
                    -------------------------------------------""");

            int option;

            while (true) {
                System.out.print("Please enter your choice: ");
                try {
                    option = Integer.parseInt(scanner.next().trim());

                    if (option >= 1 && option <= 9) {
                        break;
                    } else {
                        System.out.println("ERROR: Invalid choice. Please choose between 1 and 9.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: Invalid input. Please enter a number between 1 and 9.");
                }
            }

            switch (option) {
                case 1:
                    configureSystem(config);
                    break;
                case 2:
                    startSystem(config);
                    break;
                case 3:
                    stopSystem();
                    break;
                case 4:
                    addVendor(config);
                    break;
                case 5:
                    removeVendor();
                    break;
                case 6:
                    addCustomer(config);
                    break;
                case 7:
                    removeCustomer();
                    break;
                case 8:
                    viewSystemStatus();
                    break;
                case 9:
                    exitSystem(ticketPool);
                    System.out.println("\nSYSTEM NOTIFICATION: Exited system successfully.");
                    scanner.close();
                    return;
                default:
                    System.out.println("ERROR: Invalid selection. Please choose a valid option.");
            }
        }
    }

    private static void configureSystem(Configuration config) {
        config.startCLI(new Scanner(System.in));
        ticketPool = new TicketPool(config);
        systemConfigured = true;
    }

    private static void startSystem(Configuration config) {
        if (!systemConfigured) {
            System.out.println("ERROR: The system is not configured. Please configure the system first (Option 1).");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("\nPlease enter the number of vendors and customers to start the system./n");
        // Prompt for vendor and customer counts
        int vendorCount = getValidInput(scanner, "Enter the number of vendors: ");
        int customerCount = getValidInput(scanner, "Enter the number of customers: ");

//        int vendorCount = random.nextInt(5) + 1;
//        int customerCount = random.nextInt(20);

        // Start ticket handling operations
        ticketPool.start();

        // Create and start vendor and customer threads
        for (int i = 0; i < vendorCount; i++) {
            Vendor vendor = new Vendor(ticketPool, config);
            Thread vendorThread = new Thread(vendor);
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        for (int i = 0; i < customerCount; i++) {
            Customer customer = new Customer(ticketPool, config);
            Thread customerThread = new Thread(customer);
            customerThreads.add(customerThread);
            customerThread.start();
        }

        systemRunning = true;
    }

    // Method to stop the system
    private static void stopSystem() throws InterruptedException {
        if (!systemRunning) {
            System.out.println("ERROR: Please start the system first by configuring (Option 1 & 2).");
            return;
        }

        if (ticketPool == null) {
            System.out.println("ERROR: Ticket pool is not initialized. Please configure the system (Option 1).");
            return;
        }

        System.out.println("INFO: Stopping the system...\n");
        systemRunning = false;

        // Interrupt and stop vendor threads
        for (Thread vendorThread : vendorThreads) {
            vendorThread.interrupt();
            try {
                vendorThread.join();
            } catch (InterruptedException e) {
                System.out.println("WARNING: Vendor thread interrupted during shutdown.");
            }
        }

        // Interrupt and stop customer threads
        for (Thread customerThread : customerThreads) {
            customerThread.interrupt();
            try {
                customerThread.join();
            } catch (InterruptedException e) {
                System.out.println("WARNING: Customer thread interrupted during shutdown.");
            }
        }

        vendorThreads.clear();
        customerThreads.clear();
        systemRunning = false;
        ticketPool.stop();
    }


    private static void addVendor(Configuration config) {
        if (!systemRunning) {
            System.out.println("ERROR: Please start the system first by configuring (Option 1 & 2).");
            return;
        }
        Vendor vendor = new Vendor(ticketPool, config);
        Thread vendorThread = new Thread(vendor);
        vendorThreads.add(vendorThread);
        vendorThread.start();
        System.out.println("\nSUCCESS: A new vendor has been added.");
        Logger.log("\nSUCCESS: A new vendor has been added.");
    }

    private static void removeVendor() {
        if (!systemRunning) {
            System.out.println("ERROR: Please start the system first by configuring (Option 1 & 2).");
            return;
        }

        if (vendorThreads.isEmpty()) {
            System.out.println("\nERROR: No vendors available to remove.");
            return;
        }

        Thread vendorThread = vendorThreads.remove(vendorThreads.size() - 1);
        vendorThread.interrupt();
        System.out.println("\nSUCCESS: A vendor has been removed.");
        Logger.log("\nSUCCESS: A vendor has been removed.");
    }

    private static void addCustomer(Configuration config) {
        if (!systemRunning) {
            System.out.println("ERROR: Please start the system first by configuring (Option 1 & 2).");
            return;
        }

        Customer customer = new Customer(ticketPool, config);
        Thread customerThread = new Thread(customer);
        customerThreads.add(customerThread);
        customerThread.start();
        System.out.println("\nSUCCESS: A new customer has been added.");
        Logger.log("\nSUCCESS: A new customer has been added.");
    }

    private static void removeCustomer() {
        if (!systemRunning) {
            System.out.println("ERROR: Please start the system first by configuring (Option 1 & 2).");
            return;
        }

        if (customerThreads.isEmpty()) {
            System.out.println("\nERROR: No customers available to remove.");
            return;
        }

        Thread customerThread = customerThreads.remove(customerThreads.size() - 1);
        customerThread.interrupt();
        System.out.println("\nSUCCESS: A customer has been removed.");
    }

    private static void viewSystemStatus() {
        System.out.println("\n------------------------------------------");
        System.out.println("            CURRENT SYSTEM STATUS          ");
        System.out.println("------------------------------------------");
        System.out.println("Configured: " + systemConfigured);
        System.out.println("Running: " + systemRunning);
        System.out.println("Active Vendors: " + vendorThreads.size());
        System.out.println("Active Customers: " + customerThreads.size());
        System.out.println("Tickets Available: " + (ticketPool != null ?  ticketPool.getTotalTickets(): "N/A"));
        System.out.println("------------------------------------------");
    }

    private static void exitSystem(TicketPool ticketPool) throws InterruptedException {
        System.out.println("Exiting the system and clearing resources...");
        if (ticketPool != null) {
            ticketPool.deleteAllTransactions();
            Logger.deleteLogFile();
        }
        if (systemRunning) {
            stopSystem();
        }
    }
    private static int getValidInput(Scanner scanner, String prompt) {
        int input;
        while (true) {
            System.out.println(prompt);
            try {
                input = Integer.parseInt(scanner.next().trim());
                if (input > 0) {
                    break;
                } else {
                    System.out.println("ERROR: Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid input. Please enter a valid number.");
            }
        }
        return input;
    }
}