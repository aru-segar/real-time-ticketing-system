import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private final List<Ticket> tickets = Collections.synchronizedList(new ArrayList<>());
    //private int totalTickets;
    private final int maxTicketCapacity;
    private volatile boolean systemRunning = false;
    private final Lock lock = new ReentrantLock();
    private final Condition spaceAvailable = lock.newCondition();
    private final Condition ticketsAvailable = lock.newCondition();

    private static final String DB_URL = "jdbc:mysql://localhost:3306/transactions";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    public TicketPool (Configuration configuration) {
        //this.totalTickets = configuration.getTotalTickets();
        this.maxTicketCapacity = configuration.getMaxTicketCapacity();
        initialiseTickets(configuration.getTotalTickets());
    }

    // Initialise the ticket array with the starting number of tickets
    private void initialiseTickets(int initialTickets) {
        for (int i = 0; i < initialTickets; i++) {
            tickets.add(new Ticket());
        }
        System.out.println("\nINFO: Initialised ticket pool with " + initialTickets + " tickets.");

    }

    // Method to start ticket handling operations
    public void start() {
        lock.lock();
        try {
            if (systemRunning) {
                System.out.println("\nSYSTEM ALERT: The system is already running.");
                return;
            }
            systemRunning = true;
            System.out.println("\nSYSTEM NOTIFICATION: Ticket handling operations started.");
            ticketsAvailable.signalAll();
            spaceAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Method to stop ticket handling operations
    public void stop() {
        lock.lock();
        try {
            if (!systemRunning) {
                System.out.println("\nSYSTEM ALERT: The system is already stopped.");
                return;
            }
            systemRunning = false;
            System.out.println("\nSYSTEM NOTIFICATION: Ticket handling operations stopped.\n");
        } finally {
            lock.unlock();
        }
    }

    // Method for vendors to add tickets to the pool
    public void addTickets(int numberOfTickets, Vendor vendor) {
        lock.lock();
        try {
            while(tickets.size() + numberOfTickets > maxTicketCapacity) {
                if (!systemRunning) {
                    System.out.println("\nINFO: Vendor " + vendor.getVendorId() + " cannot add tickets. System is stopped.");
                    return;
                }
                //System.out.println("INFO: Vendor " + vendor.getVendorId() + " is waiting for space to add tickets.");
                spaceAvailable.await();
            }
            for (int i = 0; i < numberOfTickets; i++) {
                tickets.add(new Ticket());
            }
            //totalTickets += numberOfTickets;
            System.out.println("\nINFO: Vendor " + vendor.getVendorId() + " added " + numberOfTickets + " tickets.");
            Logger.log("\nINFO: Vendor " + vendor.getVendorId() + " added " + numberOfTickets + " tickets.");
            showStatus();
            saveTransaction(new Transaction("VENDOR", vendor.getVendorId(), "ADD", numberOfTickets, tickets.size()));
            ticketsAvailable.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("ERROR: Vendor " + vendor.getVendorId() + " was interrupted during ticket addition.");
        } finally {
            lock.unlock();
        }
    }

    // Method for customers to remove tickets from the pool
    public void removeTickets(int numberOfTickets, Customer customer) {
        lock.lock();
        try {
            while (tickets.size() < numberOfTickets) {
                if (!systemRunning) {
                    System.out.println("\nINFO: Customer " + customer.getCustomerId() + " cannot purchase tickets. System is stopped.");
                    return;
                }
                //System.out.println("\nNOTICE: Not enough tickets available. Customer " + customer.getCustomerId() + " is waiting...");
                ticketsAvailable.await();
            }
            tickets.subList(0, numberOfTickets).clear();
            //totalTickets -= numberOfTickets;
            System.out.println("\nINFO: Customer " + customer.getCustomerId() + " purchased " + numberOfTickets + " tickets.");
            Logger.log("\nINFO: Customer " + customer.getCustomerId() + " purchased " + numberOfTickets + " tickets.");
            showStatus();
            saveTransaction(new Transaction("CUSTOMER", customer.getCustomerId(), "PURCHASE", numberOfTickets, tickets.size()));
            spaceAvailable.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("ERROR: Customer " + customer.getCustomerId() + " was interrupted while attempting to purchase tickets.");
        } finally {
            lock.unlock();
        }
    }

    // Method to display the current status of the ticket pool
    public void showStatus() {
        System.out.println("Current pool size: " + tickets.size() + "\nRemaining pool capacity: " + (maxTicketCapacity - tickets.size()));
        Logger.log("Current pool size: " + tickets.size() + "\nRemaining pool capacity: " + (maxTicketCapacity - tickets.size()));
    }

    // Method to save transaction to the database
    private void saveTransaction(Transaction transaction) {
        String insertSQL = "INSERT INTO transactions (entity, entity_id, type, ticket_count, ticket_quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            // Map transaction attributes to placeholders in the SQL query
            statement.setString(1, transaction.getEntity());
            statement.setInt(2, transaction.getId());
            statement.setString(3, transaction.getType());
            statement.setInt(4, transaction.getTicketCount());
            statement.setInt(5, transaction.getTicketQuantity());
            // Execute the insert operation
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to save transaction: " + e.getMessage());
        }
    }

    // Method to delete all transactions from the database
    public void deleteAllTransactions() {
        String deleteSQL = "DELETE FROM transactions";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.executeUpdate();
            System.out.println("\nINFO: All transactions deleted from database.");
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to delete transactions: " + e.getMessage());
        }
    }
    public int getTotalTickets() {
        return tickets.size();
    }
}
