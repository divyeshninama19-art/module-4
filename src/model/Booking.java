package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Booking {
    private static int counter = 1000;

    private int bookingId;
    private Movie movie;
    private String showTime;
    private List<String> seats;
    private double totalAmount;
    private boolean paid;
    private LocalDateTime createdAt;

    public Booking(Movie movie, String showTime, List<String> seats) {
        this.bookingId = ++counter;
        this.movie = movie;
        this.showTime = showTime;
        this.seats = seats;
        this.totalAmount = movie.getPrice() * seats.size();
        this.paid = false;
        this.createdAt = LocalDateTime.now();
    }

    public int getBookingId() { return bookingId; }
    public Movie getMovie() { return movie; }
    public String getShowTime() { return showTime; }
    public List<String> getSeats() { return seats; }
    public double getTotalAmount() { return totalAmount; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getFormattedDate() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }
}
