package model;

public class Ticket {
    private int ticketId;
    private Booking booking;
    private String seat;

    private static int counter = 5000;

    public Ticket(Booking booking, String seat) {
        this.ticketId = ++counter;
        this.booking = booking;
        this.seat = seat;
    }

    public int getTicketId() { return ticketId; }
    public Booking getBooking() { return booking; }
    public String getSeat() { return seat; }
}
