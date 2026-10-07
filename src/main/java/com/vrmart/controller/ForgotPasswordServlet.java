package com.vrmart.controller;

import com.vrmart.dao.OtpDAO;
import com.vrmart.dao.UserDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.vrmart.service.EmailService;
import com.vrmart.service.OtpService;
import com.zaxxer.hikari.HikariDataSource;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet("/forgot-password")
public final class ForgotPasswordServlet extends HttpServlet {

    /** Password reset OTP purpose. */
    private static final String PURPOSE = "PASSWORD_RESET";

    /** Reset password page path. */
    private static final String RESET_PAGE = "/reset-password";

    /** Session attribute for reset user ID. */
    private static final String RESET_USER_ID = "resetUserId";

    /** Session attribute for reset email. */
    private static final String RESET_EMAIL = "resetEmail";

    /** Generic success message. */
    private static final String GENERIC_MESSAGE =
            "If an account exists with that email, "
                    + "a password reset code has been sent.";

    /**
     * Displays the forgot-password page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgot-password.jsp")
                .forward(request, response);
    }

    /**
     * Generates and emails a password reset OTP.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
     * @throws IOException when redirect or forwarding fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final String email = normalizeEmail(
                request.getParameter("email"));

        if (email.isEmpty()) {
            request.setAttribute(
                    "error",
                    "Please enter your email address.");
            request.getRequestDispatcher("/forgot-password.jsp")
                    .forward(request, response);
            return;
        }

        final HikariDataSource dataSource =
                getDataSource(request);

        try {
            final UserDAO userDAO = new UserDAO(dataSource);
            final User user = userDAO.findByEmail(email);

            if (user == null) {
                showGenericMessage(request, response);
                return;
            }

            final OtpDAO otpDAO = new OtpDAO(dataSource);
            final OtpService otpService = new OtpService(otpDAO);
            final String otp = otpService.createOtp(
                    user.getId(),
                    PURPOSE);

            final EmailService emailService =
                    new EmailService();

            emailService.sendOtpEmail(
                    user.getEmail(),
                    otp,
                    "VR Mart Password Reset");

            final HttpSession session =
                    request.getSession(true);

            session.setAttribute(
                    RESET_USER_ID,
                    user.getId());
            session.setAttribute(
                    RESET_EMAIL,
                    user.getEmail());

            response.sendRedirect(
                    request.getContextPath() + RESET_PAGE);
        } catch (SQLException | MessagingException exception) {
            request.setAttribute(
                    "error",
                    "Unable to send the reset code. "
                            + "Please try again.");
            request.getRequestDispatcher("/forgot-password.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Displays a generic response for unknown email addresses.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when forwarding fails
     * @throws IOException when forwarding fails
     */
    private void showGenericMessage(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute(
                "success",
                GENERIC_MESSAGE);
        request.getRequestDispatcher("/forgot-password.jsp")
                .forward(request, response);
    }

    /**
     * Gets the configured application data source.
     *
     * @param request HTTP request
     * @return Hikari data source
     * @throws ServletException when data source is unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {
        final Object attribute =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(attribute instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart data source is unavailable.");
        }

        return (HikariDataSource) attribute;
    }

    /**
     * Normalizes an email address.
     *
     * @param email submitted email
     * @return normalized email
     */
    private String normalizeEmail(final String email) {
        if (email == null) {
            return "";
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
