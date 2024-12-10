public class Transaction {
    private String entity; // Entity involved "VENDOR" or "CUSTOMER"
    private int id; // ID of customer/vendor
    private String type; // Type of transaction, e.g., "PURCHASE" or "ADD"
    private int ticketCount; // Number of tickets involved in the transaction
    private int ticketQuantity; // Current ticket pool size

    // Constructor
    public Transaction(String entity, int id, String type, int ticketCount, int ticketQuantity) {
        this.entity = entity;
        this.id = id;
        this.type = type;
        this.ticketCount = ticketCount;
        this.ticketQuantity = ticketQuantity;
    }

    // Getters
    public String getEntity() {
        return entity;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }
}
