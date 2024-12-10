import java.util.concurrent.atomic.AtomicInteger;

public class Customer implements Runnable {
    public static final AtomicInteger customerIdCounter = new AtomicInteger(1);
    private final int customerId;
    private final TicketPool ticketPool;
    private final Configuration configuration;
    private volatile boolean isRunning = true;
    // Constructor
    public Customer(TicketPool ticketPool, Configuration configuration) {
        this.ticketPool = ticketPool;
        this.configuration = configuration;
        this.customerId = customerIdCounter.getAndIncrement();
    }

    // The run method defines the thread's task
    @Override
    public void run() {
        try {
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                ticketPool.removeTickets(configuration.getCustomerRetrievalRate(), this);
                Thread.sleep(2000);
            }
            System.out.println("INFO: Customer " + customerId + " has completed ticket retrieval.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("ERROR: Customer " + customerId + " thread was interrupted during ticket retrieval.");
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public void stop() {
        isRunning = false;
    }

}
