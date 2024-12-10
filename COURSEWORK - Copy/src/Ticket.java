public class Ticket {
    private int ticketCounter = 1;
    private int ticketId;

    public Ticket () {
        this.ticketId = ticketCounter++;
    }

    public int getTicketId() {
        return ticketId;
    }
}
