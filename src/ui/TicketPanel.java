package ui;

import model.Booking;
import model.Ticket;
import service.BookingService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class TicketPanel extends JPanel {

    private MainFrame mainFrame;
    private BookingService bookingService;
    private JPanel ticketsContainer;

    public TicketPanel(MainFrame mainFrame, BookingService bookingService) {
        this.mainFrame = mainFrame;
        this.bookingService = bookingService;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setBackground(new Color(20, 20, 35));
        buildUI();
    }

    private void buildUI() {
        JLabel title = new JLabel("Your Tickets", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        ticketsContainer = new JPanel();
        ticketsContainer.setLayout(new BoxLayout(ticketsContainer, BoxLayout.Y_AXIS));
        ticketsContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(ticketsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottom.setOpaque(false);

        JButton newBookingBtn = new JButton("Book Another Ticket");
        styleButton(newBookingBtn, new Color(70, 130, 230));
        newBookingBtn.addActionListener(e -> mainFrame.showBookingPanel());
        bottom.add(newBookingBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    public void showTickets(Booking booking) {
        ticketsContainer.removeAll();

        List<Ticket> tickets = bookingService.generateTickets(booking);

        // Success header
        JLabel check = new JLabel("✔ Booking Confirmed!", SwingConstants.CENTER);
        check.setFont(new Font("SansSerif", Font.BOLD, 16));
        check.setForeground(new Color(80, 220, 100));
        check.setAlignmentX(Component.CENTER_ALIGNMENT);
        check.setBorder(new EmptyBorder(0, 0, 15, 0));
        ticketsContainer.add(check);

        for (Ticket ticket : tickets) {
            ticketsContainer.add(buildTicketCard(ticket));
            ticketsContainer.add(Box.createVerticalStrut(10));
        }

        ticketsContainer.revalidate();
        ticketsContainer.repaint();
    }

    private JPanel buildTicketCard(Ticket ticket) {
        Booking b = ticket.getBooking();

        JPanel card = new JPanel(new GridLayout(5, 2, 8, 4));
        card.setBackground(new Color(35, 35, 55));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 230), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        card.setMaximumSize(new Dimension(500, 160));

        card.add(cardLabel("Ticket #", true));   card.add(cardLabel(String.valueOf(ticket.getTicketId()), false));
        card.add(cardLabel("Movie:", true));      card.add(cardLabel(b.getMovie().getTitle(), false));
        card.add(cardLabel("Show Time:", true));  card.add(cardLabel(b.getShowTime(), false));
        card.add(cardLabel("Seat:", true));       card.add(cardLabel(ticket.getSeat(), false));
        card.add(cardLabel("Booked On:", true));  card.add(cardLabel(b.getFormattedDate(), false));

        return card;
    }

    private JLabel cardLabel(String text, boolean isKey) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", isKey ? Font.BOLD : Font.PLAIN, 13));
        l.setForeground(isKey ? new Color(150, 180, 255) : Color.WHITE);
        return l;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
