package ui;

import model.Booking;
import model.Movie;
import service.BookingService;
import service.PaymentService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private BookingPanel bookingPanel;
    private PaymentPanel paymentPanel;
    private TicketPanel ticketPanel;

    private BookingService bookingService = new BookingService();
    private PaymentService paymentService = new PaymentService();

    private static final Movie[] MOVIES = {
        new Movie(1, "Interstellar", "Sci-Fi", "2h 49m",
                new String[]{"10:00 AM", "1:30 PM", "5:00 PM", "9:00 PM"}, 120.00),
        new Movie(2, "The Dark Knight", "Action", "2h 32m",
                new String[]{"11:00 AM", "2:30 PM", "6:00 PM"}, 90.00),
        new Movie(3, "Inception", "Thriller", "2h 28m",
                new String[]{"10:30 AM", "3:00 PM", "7:30 PM"}, 110.00),
        new Movie(4, "Avatar: The Way of Water", "Adventure", "3h 12m",
                new String[]{"12:00 PM", "4:00 PM", "8:30 PM"}, 150.00),
    };

    public MainFrame() {
        setTitle("CineBook - Cinema Ticket Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        bookingPanel = new BookingPanel(this, bookingService, MOVIES);
        paymentPanel = new PaymentPanel(this, paymentService);
        ticketPanel  = new TicketPanel(this, bookingService);

        cardPanel.add(bookingPanel, "BOOKING");
        cardPanel.add(paymentPanel, "PAYMENT");
        cardPanel.add(ticketPanel,  "TICKET");

        add(cardPanel);
        cardLayout.show(cardPanel, "BOOKING");
    }

    public void showBookingPanel() {
        cardLayout.show(cardPanel, "BOOKING");
    }

    public void showPaymentPanel(Booking booking) {
        paymentPanel.setBooking(booking);
        cardLayout.show(cardPanel, "PAYMENT");
    }

    public void showTicketPanel(Booking booking) {
        ticketPanel.showTickets(booking);
        cardLayout.show(cardPanel, "TICKET");
    }
}
