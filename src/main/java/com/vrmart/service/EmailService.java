package com.vrmart.service;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Service for sending VR Mart email messages.
 */
public final class EmailService {

    /** Default SMTP port. */
    private static final String DEFAULT_SMTP_PORT = "587";

    /** SMTP host environment variable. */
    private static final String SMTP_HOST = "VRMART_SMTP_HOST";

    /** SMTP port environment variable. */
    private static final String SMTP_PORT = "VRMART_SMTP_PORT";

    /** SMTP username environment variable. */
    private static final String SMTP_USERNAME = "VRMART_SMTP_USERNAME";

    /** SMTP password environment variable. */
    private static final String SMTP_PASSWORD = "VRMART_SMTP_PASSWORD";

    /** SMTP sender environment variable. */
    private static final String SMTP_FROM = "VRMART_SMTP_FROM";

    /**
     * Sends an OTP email.
     *
     * @param recipient recipient email address
     * @param otp one-time password
     * @param subject email subject
     * @throws MessagingException when email delivery fails
     */
    public void sendOtpEmail(
            final String recipient,
            final String otp,
            final String subject)
            throws MessagingException {

        final String host = requiredEnvironment(SMTP_HOST);
        final String port = environmentOrDefault(
                SMTP_PORT,
                DEFAULT_SMTP_PORT);
        final String username = requiredEnvironment(SMTP_USERNAME);
        final String password = requiredEnvironment(SMTP_PASSWORD);
        final String from = environmentOrDefault(
                SMTP_FROM,
                username);

        System.out.println("========================================");
        System.out.println("VR MART OTP EMAIL");
        System.out.println("SMTP Host : " + host);
        System.out.println("SMTP Port : " + port);
        System.out.println("SMTP User : " + username);
        System.out.println("From      : " + from);
        System.out.println("Recipient : " + recipient);
        System.out.println("========================================");

        final Properties properties = new Properties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        /*
         * Enable JavaMail debug output.
         * This helps us see SMTP connection/authentication errors
         * in the Tomcat console.
         */
        properties.put("mail.debug", "true");

        final Session session = Session.getInstance(
                properties,
                new javax.mail.Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication
                            getPasswordAuthentication() {

                        return new javax.mail.PasswordAuthentication(
                                username,
                                password);
                    }
                });

        final Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));

        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(recipient));

        message.setSubject(subject);

        message.setText(
                "Your VR Mart verification code is: "
                        + otp
                        + "\n\nThis code expires in 10 minutes."
                        + "\n\nIf you did not request this code, "
                        + "please ignore this email.");

        try {
            System.out.println("=== VR MART OTP: SENDING ===");

            Transport.send(message);

            System.out.println("=== VR MART OTP: SENT SUCCESSFULLY ===");

        } catch (MessagingException exception) {

            System.err.println("========================================");
            System.err.println("VR MART OTP EMAIL FAILED");
            System.err.println("Recipient: " + recipient);
            System.err.println("SMTP Host: " + host);
            System.err.println("SMTP Port: " + port);
            System.err.println("SMTP User: " + username);
            System.err.println("========================================");

            exception.printStackTrace();

            throw exception;
        }
    }

    /**
     * Reads a required environment variable.
     *
     * @param name environment variable name
     * @return environment variable value
     */
    private String requiredEnvironment(final String name) {
        final String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required environment variable: " + name);
        }

        return value;
    }

    /**
     * Reads an environment variable with a fallback value.
     *
     * @param name environment variable name
     * @param defaultValue fallback value
     * @return environment variable value or fallback
     */
    private String environmentOrDefault(
            final String name,
            final String defaultValue) {

        final String value = System.getenv(name);

        return value == null || value.isBlank()
                ? defaultValue
                : value;
    }
}
