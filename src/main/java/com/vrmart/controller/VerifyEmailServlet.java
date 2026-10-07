package com.vrmart.controller;

import com.vrmart.dao.OtpDAO;
import com.vrmart.dao.UserDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.vrmart.service.OtpService;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles email verification using OTP.
 */
@WebServlet("/verify-email")
public final class VerifyEmailServlet extends HttpServlet {
    /** Verification page path. */
    private static final String VERIFY_EMAIL_PAGE = "/verify-email.jsp";
    /** Verification email session attribute. */
    private static final String VERIFICATION_EMAIL = "verificationEmail";
    /** OTP verification purpose. */
    private static final String EMAIL_VERIFICATION = "EMAIL_VERIFICATION";
    /** OTP request parameter. */
    private static final String OTP_PARAMETER = "otp";
    /** Error request attribute. */
    private static final String ERROR_ATTRIBUTE = "error";
    /** Success session attribute. */
    private static final String SUCCESS_ATTRIBUTE = "success";

    /**
     * Displays the email verification page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException servlet processing error
     * @throws IOException input/output error
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute(VERIFICATION_EMAIL) == null) {
            response.sendRedirect(
                    request.getContextPath() + "/buyer/login");
            return;
        }

        request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                .forward(request, response);
    }

    /**
     * Verifies the submitted email OTP.
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
        final String otp = request.getParameter(OTP_PARAMETER);

        if (email == null || email.isBlank()) {
            request.setAttribute(
                    ERROR_ATTRIBUTE,
                    "Verification session expired. Please register again.");
            request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                    .forward(request, response);
            return;
        }

        if (otp == null || !otp.matches("\\d{6}")) {
            request.setAttribute(
                    ERROR_ATTRIBUTE,
                    "Enter a valid 6-digit verification code.");
            request.setAttribute(VERIFICATION_EMAIL, email);
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
                        "Verification could not be completed.");
                request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                        .forward(request, response);
                return;
            }

            if (user.isEmailVerified()) {
                clearVerificationSession(session);
                redirectToLogin(request, response, user);
                return;
            }

            final OtpService otpService =
                    new OtpService(new OtpDAO(dataSource));

            final boolean verified = otpService.verifyOtp(
                    user.getId(),
                    EMAIL_VERIFICATION,
                    otp);

            if (!verified) {
                request.setAttribute(
                        ERROR_ATTRIBUTE,
                        "Invalid or expired verification code.");
                request.setAttribute(VERIFICATION_EMAIL, email);
                request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                        .forward(request, response);
                return;
            }

            userDAO.markEmailVerified(user.getId());
            clearVerificationSession(session);
            session.setAttribute(
                    SUCCESS_ATTRIBUTE,
                    "Email verified successfully. Please log in.");
            redirectToLogin(request, response, user);
        } catch (SQLException exception) {
            request.setAttribute(
                    ERROR_ATTRIBUTE,
                    "Unable to verify email. Please try again.");
            request.setAttribute(VERIFICATION_EMAIL, email);
            request.getRequestDispatcher(VERIFY_EMAIL_PAGE)
                    .forward(request, response);
        }
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
     * Clears verification session data.
     *
     * @param session current HTTP session
     */
    private void clearVerificationSession(
            final HttpSession session) {
        session.removeAttribute(VERIFICATION_EMAIL);
    }

    /**
     * Redirects the user to the appropriate login page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param user verified user
     * @throws IOException input/output error
     */
    private void redirectToLogin(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final User user)
            throws IOException {
        final String contextPath = request.getContextPath();
        final String loginPath;

        if (User.ROLE_SELLER.equals(user.getRole())) {
            loginPath = "/seller/login";
        } else if (User.ROLE_ADMIN.equals(user.getRole())) {
            loginPath = "/login";
        } else {
            loginPath = "/buyer/login";
        }

        response.sendRedirect(contextPath + loginPath);
    }
}
