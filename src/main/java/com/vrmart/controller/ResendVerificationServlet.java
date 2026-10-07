package com.vrmart.controller;

import com.vrmart.dao.OtpDAO;
import com.vrmart.dao.UserDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.vrmart.service.EmailService;
import com.vrmart.service.OtpService;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.SQLException;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles resending email verification OTPs.
 */
@WebServlet("/resend-verification")
public final class ResendVerificationServlet extends HttpServlet {
    /** Verification page path. */
    private static final String VERIFY_EMAIL_PAGE = "/verify-email.jsp";
    /** Verification email session attribute. */
    private static final String VERIFICATION_EMAIL = "verificationEmail";
    /** OTP verification purpose. */
    private static final String EMAIL_VERIFICATION = "EMAIL_VERIFICATION";
    /** Error request attribute. */
    private static final String ERROR_ATTRIBUTE = "error";
    /** Success request attribute. */
    private static final String SUCCESS_ATTRIBUTE = "success";

    /**
     * Resends a verification OTP to the registered email.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException servlet processing error
     * @throws IOException input/output error
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/buyer/login");
            return;
        }

        final String email = getVerificationEmail(session);

        if (email == null || email.isBlank()) {
            request.setAttribute(
                    ERROR_ATTRIBUTE,
                    "Verification session expired. Please register again.");
            request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                    .forward(request, response);
            return;
        }

        final HikariDataSource dataSource =
                (HikariDataSource) getServletContext()
                        .getAttribute(
                                DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (dataSource == null) {
            request.setAttribute(
                    ERROR_ATTRIBUTE,
                    "Database connection is unavailable.");
            request.setAttribute(VERIFICATION_EMAIL, email);
            request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                    .forward(request, response);
            return;
        }

        try {
            final UserDAO userDAO = new UserDAO(dataSource);
            final User user = userDAO.findByEmail(email);

            if (user == null) {
                request.setAttribute(
                        ERROR_ATTRIBUTE,
                        "Unable to resend verification code.");
                request.setAttribute(VERIFICATION_EMAIL, email);
                request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                        .forward(request, response);
                return;
            }

            if (user.isEmailVerified()) {
                session.removeAttribute(VERIFICATION_EMAIL);
                response.sendRedirect(
                        request.getContextPath() + getLoginPath(user));
                return;
            }

            final OtpService otpService =
                    new OtpService(new OtpDAO(dataSource));
            final String otp = otpService.createOtp(
                    user.getId(), EMAIL_VERIFICATION);

            final EmailService emailService = new EmailService();
            emailService.sendOtpEmail(
                    user.getEmail(),
                    otp,
                    "VR Mart Email Verification");

            session.setAttribute(
                    VERIFICATION_EMAIL,
                    user.getEmail());
            request.setAttribute(
                    SUCCESS_ATTRIBUTE,
                    "A new verification code has been sent to your email.");
            request.setAttribute(
                    VERIFICATION_EMAIL,
                    user.getEmail());
            request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                    .forward(request, response);
        } catch (SQLException | MessagingException exception) {
            request.setAttribute(
                    ERROR_ATTRIBUTE,
                    "Unable to send verification code. Please try again.");
            request.setAttribute(VERIFICATION_EMAIL, email);
            request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                    .forward(request, response);
        }
    }

    /**
     * Handles direct GET requests by returning to verification.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException input/output error
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {
        response.sendRedirect(
                request.getContextPath() + "/verify-email");
    }

    /**
     * Gets the verification email from the session.
     *
     * @param session current HTTP session
     * @return verification email or null
     */
    private String getVerificationEmail(
            final HttpSession session) {
        final Object value =
                session.getAttribute(VERIFICATION_EMAIL);

        if (!(value instanceof String)) {
            return null;
        }

        final String email = ((String) value).trim();
        return email.isEmpty() ? null : email.toLowerCase();
    }

    /**
     * Gets the login route for the specified user role.
     *
     * @param user verified user
     * @return login route
     */
    private String getLoginPath(final User user) {
        if (User.ROLE_SELLER.equals(user.getRole())) {
            return "/seller/login";
        }

        if (User.ROLE_ADMIN.equals(user.getRole())) {
            return "/login";
        }

        return "/buyer/login";
    }
}
