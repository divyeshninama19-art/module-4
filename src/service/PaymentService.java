package service;

import model.Booking;

public class PaymentService {

    public enum PaymentMethod { CREDIT_CARD, DEBIT_CARD, UPI }

    /**
     * Simulates payment processing.
     * In a real app, this would call a payment gateway API.
     */
    public boolean processPayment(Booking booking, PaymentMethod method, String paymentDetail) {
        if (method == PaymentMethod.UPI) {
            // UPI pays via QR scan, no detail required
            booking.setPaid(true);
            return true;
        }
        if (paymentDetail == null || paymentDetail.trim().isEmpty()) return false;
        if (method == PaymentMethod.CREDIT_CARD || method == PaymentMethod.DEBIT_CARD) {
            if (paymentDetail.replaceAll("\\s", "").length() != 16) return false;
        }
        booking.setPaid(true);
        return true;
    }
}
