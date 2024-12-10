import java.util.concurrent.atomic.AtomicInteger;

public class Vendor implements Runnable {
    public static final AtomicInteger vendorIdCounter = new AtomicInteger(1);
    private final int vendorId; // Vendor's unique ID
    private final TicketPool ticketPool;
    private final Configuration configuration;
    private volatile boolean isRunning = true;

    // Constructor
    public Vendor(TicketPool ticketPool, Configuration configuration) {
        this.ticketPool = ticketPool;
        this.configuration = configuration;
        this.vendorId = vendorIdCounter.getAndIncrement();
    }

    // The run method defines the thread's task
    @Override
    public void run() {
        try {
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                ticketPool.addTickets(configuration.getTicketReleaseRate(), this);
                Thread.sleep(2000);
            }
            System.out.println("INFO: Vendor " + vendorId + " has completed ticket release operations.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("ERROR: Vendor " + vendorId + " thread was interrupted during ticket release.");
        }
    }
    public int getVendorId() {
        return vendorId;
    }

    public void stop() {
        isRunning = false;
    }
}
