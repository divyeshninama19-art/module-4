package ui;

import model.Booking;
import service.PaymentService;
import service.PaymentService.PaymentMethod;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Random;

public class PaymentPanel extends JPanel {

    private MainFrame mainFrame;
    private PaymentService paymentService;
    private Booking booking;

    private JComboBox<String> methodCombo;
    private JTextField detailField;
    private JLabel summaryLabel;
    private JLabel hintLabel;
    private JLabel detailRowLabel;
    private JPanel qrWrapper;
    private JLabel qrAmountLabel;
    private JPanel detailRow;

    public PaymentPanel(MainFrame mainFrame, PaymentService paymentService) {
        this.mainFrame = mainFrame;
        this.paymentService = paymentService;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 40, 15, 40));
        setBackground(new Color(20, 20, 35));
        buildUI();
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        summaryLabel.setText(buildSummary());
        detailField.setText("");
        if (qrAmountLabel != null)
            qrAmountLabel.setText(String.format("\u20B9%.2f", booking.getTotalAmount()));
    }

    private void buildUI() {
        JLabel title = new JLabel("Payment", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        summaryLabel = new JLabel();
        summaryLabel.setForeground(new Color(180, 220, 255));
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        summaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryLabel.setBorder(new EmptyBorder(5, 0, 12, 0));
        center.add(summaryLabel);

        JPanel row1 = formRow("Payment Method:");
        methodCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "UPI"});
        styleCombo(methodCombo);
        methodCombo.addActionListener(e -> updateHint());
        row1.add(methodCombo);
        center.add(row1);
        center.add(Box.createVerticalStrut(10));

        detailRow = formRow("Card Number:");
        detailRowLabel = (JLabel) detailRow.getComponent(0);
        detailField = new JTextField(20);
        styleField(detailField);
        detailRow.add(detailField);
        center.add(detailRow);
        center.add(Box.createVerticalStrut(6));

        hintLabel = new JLabel("Enter 16-digit card number (no spaces)");
        hintLabel.setForeground(new Color(150, 150, 150));
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(hintLabel);
        center.add(Box.createVerticalStrut(8));

        // QR wrapper (shown only for UPI)
        qrWrapper = new JPanel();
        qrWrapper.setLayout(new BoxLayout(qrWrapper, BoxLayout.Y_AXIS));
        qrWrapper.setOpaque(false);
        qrWrapper.setVisible(false);

        JLabel qrTitle = new JLabel("Scan to Pay via UPI", SwingConstants.CENTER);
        qrTitle.setForeground(new Color(100, 220, 100));
        qrTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        qrTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        QRCodePanel qrCode = new QRCodePanel(160);
        qrCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        qrAmountLabel = new JLabel("\u20B90.00", SwingConstants.CENTER);
        qrAmountLabel.setForeground(Color.WHITE);
        qrAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        qrAmountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel upiIdLabel = new JLabel("UPI: cinebook@ybl", SwingConstants.CENTER);
        upiIdLabel.setForeground(new Color(180, 180, 255));
        upiIdLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        upiIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        qrWrapper.add(qrTitle);
        qrWrapper.add(Box.createVerticalStrut(6));
        qrWrapper.add(qrCode);
        qrWrapper.add(Box.createVerticalStrut(4));
        qrWrapper.add(qrAmountLabel);
        qrWrapper.add(upiIdLabel);
        center.add(qrWrapper);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottom.setOpaque(false);

        JButton backBtn = new JButton("< Back");
        styleButton(backBtn, new Color(80, 80, 100));
        backBtn.addActionListener(e -> mainFrame.showBookingPanel());
        bottom.add(backBtn);

        JButton payBtn = new JButton("Pay Now");
        styleButton(payBtn, new Color(60, 180, 80));
        payBtn.addActionListener(e -> processPayment());
        bottom.add(payBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void updateHint() {
        int idx = methodCombo.getSelectedIndex();
        detailField.setText("");
        boolean isUpi = (idx == 2);
        qrWrapper.setVisible(isUpi);
        detailRow.setVisible(!isUpi);
        hintLabel.setVisible(!isUpi);
        if (!isUpi) {
            hintLabel.setText("Enter 16-digit card number (no spaces)");
            detailRowLabel.setText("Card Number:");
        }
        revalidate();
        repaint();
    }

    private void processPayment() {
        if (booking == null) return;
        PaymentMethod method = PaymentMethod.values()[methodCombo.getSelectedIndex()];
        String detail = detailField.getText().trim();
        boolean success = paymentService.processPayment(booking, method, detail);
        if (success) {
            mainFrame.showTicketPanel(booking);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Payment failed. Please check your details and try again.",
                    "Payment Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildSummary() {
        if (booking == null) return "";
        return String.format(
            "<html><div style='text-align:center'>Booking #%d &nbsp;|&nbsp; %s &nbsp;|&nbsp; %s" +
            "<br>Seats: %s &nbsp;|&nbsp; <b>Total: \u20B9%.2f</b></div></html>",
            booking.getBookingId(), booking.getMovie().getTitle(),
            booking.getShowTime(), String.join(", ", booking.getSeats()),
            booking.getTotalAmount());
    }

    private JPanel formRow(String labelText) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(120, 28));
        row.add(lbl);
        return row;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setBackground(new Color(40, 40, 60));
        combo.setForeground(Color.WHITE);
        combo.setPreferredSize(new Dimension(200, 28));
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(40, 40, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Draws a fake QR code using a fixed-seed random grid + real finder patterns
    static class QRCodePanel extends JPanel {
        private static final int MODULES = 25;
        private final int size;
        private final boolean[][] grid;

        QRCodePanel(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setOpaque(false);
            grid = new boolean[MODULES][MODULES];
            Random rand = new Random(42);
            for (int r = 0; r < MODULES; r++)
                for (int c = 0; c < MODULES; c++)
                    grid[r][c] = rand.nextBoolean();
            drawFinder(0, 0);
            drawFinder(0, MODULES - 7);
            drawFinder(MODULES - 7, 0);
        }

        private void drawFinder(int row, int col) {
            for (int r = 0; r < 7; r++)
                for (int c = 0; c < 7; c++) {
                    boolean border = (r == 0 || r == 6 || c == 0 || c == 6);
                    boolean inner = (r >= 2 && r <= 4 && c >= 2 && c <= 4);
                    grid[row + r][col + c] = border || inner;
                }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int cellSize = size / MODULES;
            int padding = (size - cellSize * MODULES) / 2;
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, size, size, 10, 10);
            g2.setColor(Color.BLACK);
            for (int r = 0; r < MODULES; r++)
                for (int c = 0; c < MODULES; c++)
                    if (grid[r][c])
                        g2.fillRect(padding + c * cellSize, padding + r * cellSize, cellSize, cellSize);
        }
    }
}
