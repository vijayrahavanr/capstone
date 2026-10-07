package com.vrmart.controller;

import com.vrmart.dao.OtpDAO;
import com.vrmart.dao.UserDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.vrmart.service.AuthService;
import com.vrmart.service.EmailService;
import com.vrmart.service.OtpService;
import com.zaxxer.hikari.HikariDataSource;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Handles generic VR Mart buyer registration and email verification.
 */
@WebServlet("/register")
public final class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /** Registration page path. */
    private static final String REGISTER_PAGE = "/register.jsp";
    /** Email verification endpoint. */
    private static final String VERIFY_PAGE = "/verify-email";
    /** Session attribute containing the verification email. */
    private static final String VERIFICATION_EMAIL = "verificationEmail";
    /** OTP purpose used for email verification. */
    private static final String PURPOSE = "EMAIL_VERIFICATION";
    /** Subject used for verification emails. */
    private static final String VERIFY_SUBJECT =
            "VR Mart Email Verification";
    /** Registration success message. */
    private static final String SUCCESS_MESSAGE =
            "Account created. Please verify your email.";
    /** Message shown when email delivery fails. */
    private static final String EMAIL_ERROR =
            "Account created, but verification email could not be sent. "
            + "Please try again.";
    /** Message shown when database processing fails. */
    private static final String DATABASE_ERROR =
            "Unable to create account. Please try again.";

    /**
     * Processes a new account registration.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
     * @throws IOException when request processing fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        final String username = request.getParameter("username");
        final String email = request.getParameter("email");
        final String phone = request.getParameter("phone");
        final String password = request.getParameter("password");
        final String confirmPassword =
                request.getParameter("confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute(
                    "error",
                    "Passwords do not match.");
            request.getRequestDispatcher(REGISTER_PAGE)
                    .forward(request, response);
            return;
        }

        try {
            final HikariDataSource dataSource = getDataSource(request);
            final UserDAO userDAO = new UserDAO(dataSource);
            final AuthService authService = new AuthService(userDAO);
            final User user = authService.register(
                    username,
                    email,
                    phone,
                    password);
            final String normalizedEmail =
                    user.getEmail().trim().toLowerCase(Locale.ROOT);
            final OtpService otpService =
                    new OtpService(new OtpDAO(dataSource));
            final String otp =
                    otpService.createOtp(user.getId(), PURPOSE);
            final EmailService emailService = new EmailService();

            emailService.sendOtpEmail(
                    normalizedEmail,
                    otp,
                    VERIFY_SUBJECT);

            request.getSession(true).setAttribute(
                    VERIFICATION_EMAIL,
                    normalizedEmail);
            request.getSession(true).setAttribute(
                    "registrationMessage",
                    SUCCESS_MESSAGE);

            response.sendRedirect(
                    request.getContextPath() + VERIFY_PAGE);
        } catch (IllegalArgumentException exception) {
            request.setAttribute(
                    "error",
                    exception.getMessage());
            request.getRequestDispatcher(REGISTER_PAGE)
                    .forward(request, response);
        } catch (SQLException | MessagingException exception) {
            request.setAttribute(
                    "error",
                    exception instanceof MessagingException
                            ? EMAIL_ERROR
                            : DATABASE_ERROR);
            request.getRequestDispatcher(REGISTER_PAGE)
                    .forward(request, response);
        }
    }

    /**
     * Displays the registration page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when forwarding fails
     * @throws IOException when request processing fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(REGISTER_PAGE)
                .forward(request, response);
    }

    /**
     * Retrieves the application data source.
     *
     * @param request HTTP request
     * @return configured Hikari data source
     * @throws ServletException when the data source is unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {
        final Object dataSource = request
                .getServletContext()
                .getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(dataSource instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (HikariDataSource) dataSource;
    }
}
