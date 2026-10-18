package service;

import model.Booking;
import model.Movie;
import model.Ticket;

import java.util.*;

public class BookingService {
    // Tracks booked seats per movie+showtime
    private Map<String, Set<String>> bookedSeatsMap = new HashMap<>();
    private List<Booking> allBookings = new ArrayList<>();

    public Set<String> getBookedSeats(Movie movie, String showTime) {
        String key = movie.getId() + "_" + showTime;
        return bookedSeatsMap.getOrDefault(key, new HashSet<>());
    }

    public Booking createBooking(Movie movie, String showTime, List<String> selectedSeats) {
        String key = movie.getId() + "_" + showTime;
        Set<String> booked = bookedSeatsMap.computeIfAbsent(key, k -> new HashSet<>());
        booked.addAll(selectedSeats);

        Booking booking = new Booking(movie, showTime, selectedSeats);
        allBookings.add(booking);
        return booking;
    }

    public List<Ticket> generateTickets(Booking booking) {
        List<Ticket> tickets = new ArrayList<>();
        for (String seat : booking.getSeats()) {
            tickets.add(new Ticket(booking, seat));
        }
        return tickets;
    }

    public List<Booking> getAllBookings() {
        return Collections.unmodifiableList(allBookings);
    }
}
