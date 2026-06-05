package com.utility.billing.service;

import com.utility.billing.entity.Bill;
import com.utility.billing.entity.Customer;
import com.utility.billing.entity.Payment;
import com.utility.billing.entity.User;

/**
 * Service interface for preparing and sending email notifications.
 * Emails are processed asynchronously.
 */
public interface EmailService {

    /**
     * Sends a registration email verification link to a user.
     *
     * @param user  the user registration record
     * @param token the generated verification token string
     */
    void sendVerificationEmail(User user, String token);

    /**
     * Sends a password reset token link to a user.
     *
     * @param user  the user requesting a password reset
     * @param token the generated password reset token string
     */
    void sendPasswordResetEmail(User user, String token);

    /**
     * Sends a notification to a customer about a newly generated bill.
     *
     * @param customer the customer entity
     * @param bill     the bill details
     */
    void sendBillNotificationEmail(Customer customer, Bill bill);

    /**
     * Sends a notification to a customer acknowledging a received payment.
     *
     * @param customer the customer entity
     * @param payment  the payment details
     */
    void sendPaymentNotificationEmail(Customer customer, Payment payment);
}
