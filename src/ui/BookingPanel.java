package ui;

import model.Booking;
import model.Movie;
import service.BookingService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BookingPanel extends JPanel {

    private BookingService bookingService;
    private MainFrame mainFrame;

    private JComboBox<Movie> movieCombo;
    private JComboBox<String> showTimeCombo;
    private JPanel seatPanel;
    private Map<String, JToggleButton> seatButtons = new LinkedHashMap<>();
    private JLabel totalLabel;
    private Movie[] movies;

    public BookingPanel(MainFrame mainFrame, BookingService bookingService, Movie[] movies) {
        this.mainFrame = mainFrame;
        this.bookingService = bookingService;
        this.movies = movies;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(20, 20, 35));
        buildUI();
    }

    private void buildUI() {
        // Top: title
        JLabel title = new JLabel("Book Your Seats", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Center
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        // Movie & showtime selection
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 10, 8));
        selectionPanel.setOpaque(false);

        selectionPanel.add(styledLabel("Movie:"));
        movieCombo = new JComboBox<>(movies);
        styleCombo(movieCombo);
        selectionPanel.add(movieCombo);

        selectionPanel.add(styledLabel("Show Time:"));
        showTimeCombo = new JComboBox<>();
        styleCombo(showTimeCombo);
        selectionPanel.add(showTimeCombo);

        // listeners added after full UI init to avoid NPE during construction

        center.add(selectionPanel, BorderLayout.NORTH);

        // Seat grid
        JLabel seatTitle = styledLabel("Select Seats (click to select):");
        seatTitle.setBorder(new EmptyBorder(10, 0, 4, 0));

        seatPanel = new JPanel(new GridLayout(6, 10, 5, 5));
        seatPanel.setOpaque(false);
        buildSeatGrid();

        JPanel seatWrapper = new JPanel(new BorderLayout());
        seatWrapper.setOpaque(false);
        seatWrapper.add(seatTitle, BorderLayout.NORTH);

        // Screen indicator
        JLabel screen = new JLabel("[ SCREEN ]", SwingConstants.CENTER);
        screen.setForeground(new Color(180, 180, 255));
        screen.setFont(new Font("SansSerif", Font.ITALIC, 12));
        screen.setBorder(new EmptyBorder(6, 0, 6, 0));
        seatWrapper.add(screen, BorderLayout.SOUTH);
        seatWrapper.add(seatPanel, BorderLayout.CENTER);

        center.add(seatWrapper, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setForeground(new Color(100, 220, 100));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottom.add(totalLabel);

        JButton proceedBtn = new JButton("Proceed to Payment →");
        styleButton(proceedBtn, new Color(70, 130, 230));
        proceedBtn.addActionListener(e -> proceedToPayment());
        bottom.add(proceedBtn);
        add(bottom, BorderLayout.SOUTH);

        // Now safe to attach listeners and populate
        movieCombo.addActionListener(e -> refreshShowTimes());
        showTimeCombo.addActionListener(e -> refreshSeats());
        refreshShowTimes();
    }

    private void refreshShowTimes() {
        Movie selected = (Movie) movieCombo.getSelectedItem();
        showTimeCombo.removeAllItems();
        if (selected != null) {
            for (String t : selected.getShowTimes()) showTimeCombo.addItem(t);
        }
        refreshSeats();
    }

    private void refreshSeats() {
        buildSeatGrid();
        updateTotal();
    }

    private void buildSeatGrid() {
        if (seatPanel == null) return;
        seatPanel.removeAll();
        seatButtons.clear();
        Movie movie = (Movie) movieCombo.getSelectedItem();
        String showTime = (String) showTimeCombo.getSelectedItem();
        if (movie == null || showTime == null) { seatPanel.revalidate(); seatPanel.repaint(); return; }

        Set<String> booked = bookingService.getBookedSeats(movie, showTime);
        String[] rows = {"A", "B", "C", "D", "E", "F"};

        for (String row : rows) {
            for (int col = 1; col <= 10; col++) {
                String seatId = row + col;
                JToggleButton btn = new JToggleButton(seatId);
                btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
                btn.setFocusPainted(false);
                if (booked.contains(seatId)) {
                    btn.setEnabled(false);
                    btn.setBackground(new Color(180, 60, 60));
                    btn.setForeground(Color.WHITE);
                    btn.setToolTipText("Already booked");
                } else {
                    btn.setBackground(new Color(50, 180, 100));
                    btn.setForeground(Color.WHITE);
                    btn.addActionListener(e -> {
                        btn.setBackground(btn.isSelected() ? new Color(70, 130, 230) : new Color(50, 180, 100));
                        updateTotal();
                    });
                }
                seatButtons.put(seatId, btn);
                seatPanel.add(btn);
            }
        }
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void updateTotal() {
        Movie movie = (Movie) movieCombo.getSelectedItem();
        long selected = seatButtons.values().stream().filter(JToggleButton::isSelected).count();
        double total = movie != null ? selected * movie.getPrice() : 0;
        totalLabel.setText(String.format("Total: ₹%.2f", total));
    }

    private void proceedToPayment() {
        Movie movie = (Movie) movieCombo.getSelectedItem();
        String showTime = (String) showTimeCombo.getSelectedItem();
        if (movie == null || showTime == null) return;

        List<String> selected = new ArrayList<>();
        seatButtons.forEach((id, btn) -> { if (btn.isSelected()) selected.add(id); });

        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat.", "No Seats Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = bookingService.createBooking(movie, showTime, selected);
        mainFrame.showPaymentPanel(booking);
    }

    // --- Styling helpers ---
    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(new Color(40, 40, 60));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
