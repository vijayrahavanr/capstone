package com.vrmart.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.mail.MessagingException;

/**
 * Service for sending VR Mart email messages.
 *
 * Uses the Resend HTTPS API instead of SMTP so it works
 * on hosting platforms where outbound SMTP ports are restricted.
 */
public final class EmailService {

    /** Resend API endpoint. */
    private static final String RESEND_API_URL =
            "https://api.resend.com/emails";

    /** Resend API key environment variable. */
    private static final String RESEND_API_KEY =
            "RESEND_API_KEY";

    /** Sender email environment variable. */
    private static final String RESEND_FROM =
            "RESEND_FROM";

    /** Successful HTTP status lower bound. */
    private static final int HTTP_SUCCESS_MIN = 200;

    /** Successful HTTP status upper bound. */
    private static final int HTTP_SUCCESS_MAX = 300;

    /**
     * Sends an OTP email through Resend.
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

        final String apiKey =
                requiredEnvironment(RESEND_API_KEY);

        final String from =
                requiredEnvironment(RESEND_FROM);

        System.out.println("========================================");
        System.out.println("VR MART OTP EMAIL");
        System.out.println("Email Provider : Resend HTTPS API");
        System.out.println("From          : " + from);
        System.out.println("Recipient     : " + recipient);
        System.out.println("========================================");

        final String text =
                "Your VR Mart verification code is: "
                        + otp
                        + "\n\nThis code expires in 10 minutes."
                        + "\n\nIf you did not request this code, "
                        + "please ignore this email.";

        final String requestBody =
                "{"
                        + "\"from\":\"" + jsonEscape(from) + "\","
                        + "\"to\":[\"" + jsonEscape(recipient) + "\"],"
                        + "\"subject\":\"" + jsonEscape(subject) + "\","
                        + "\"text\":\"" + jsonEscape(text) + "\""
                        + "}";

        final HttpClient client =
                HttpClient.newHttpClient();

        final HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(RESEND_API_URL))
                        .header(
                                "Authorization",
                                "Bearer " + apiKey)
                        .header(
                                "Content-Type",
                                "application/json")
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(requestBody))
                        .build();

        try {
            System.out.println("=== VR MART OTP: SENDING ===");

            final HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            final int statusCode =
                    response.statusCode();

            if (statusCode >= HTTP_SUCCESS_MIN
                    && statusCode < HTTP_SUCCESS_MAX) {

                System.out.println(
                        "=== VR MART OTP: SENT SUCCESSFULLY ===");

                return;
            }

            System.err.println(
                    "VR MART OTP EMAIL FAILED");
            System.err.println(
                    "Resend HTTP Status: " + statusCode);
            System.err.println(
                    "Resend Response: " + response.body());

            throw new MessagingException(
                    "Resend email API returned HTTP "
                            + statusCode);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new MessagingException(
                    "Email sending was interrupted.",
                    exception);

        } catch (IOException exception) {

            throw new MessagingException(
                    "Unable to connect to Resend email API.",
                    exception);
        }
    }

    /**
     * Reads a required environment variable.
     *
     * @param name environment variable name
     * @return environment variable value
     */
    private String requiredEnvironment(
            final String name) {

        final String value =
                System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required environment variable: "
                            + name);
        }

        return value;
    }

    /**
     * Escapes a string for JSON.
     *
     * @param value input string
     * @return JSON-safe string
     */
    private String jsonEscape(
            final String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
