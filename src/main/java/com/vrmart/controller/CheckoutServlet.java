package com.vrmart.controller;

import com.vrmart.dao.CartDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.CartItem;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * Handles buyer checkout and prepares mock payment confirmation.
 */
@WebServlet("/buyer/checkout")
public final class CheckoutServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Checkout JSP page. */
    private static final String CHECKOUT_PAGE =
            "/buyer/checkout.jsp";

    /** Buyer cart URL. */
    private static final String CART_URL = "/buyer/cart";

    /** Payment confirmation URL. */
    private static final String PAYMENT_URL =
            "/buyer/payment-confirmation";

    /** Maximum customer name length. */
    private static final int MAX_NAME_LENGTH = 100;

    /** Phone length. */
    private static final int PHONE_LENGTH = 10;

    /** Maximum delivery address length. */
    private static final int MAX_ADDRESS_LENGTH = 500;

    /** Maximum landmark length. */
    private static final int MAX_LANDMARK_LENGTH = 200;

    /**
     * Displays the checkout page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session =
                request.getSession(false);

        if (!isBuyer(session)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/buyer/login");
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        try {
            final CartDAO cartDAO =
                    new CartDAO(getDataSource(request));

            final List<CartItem> cartItems =
                    cartDAO.findByBuyer(user.getId());

            if (cartItems.isEmpty()) {
                response.sendRedirect(
                        request.getContextPath()
                                + CART_URL);
                return;
            }

            request.setAttribute(
                    "cartItems",
                    cartItems);

            request.getRequestDispatcher(
                    CHECKOUT_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load checkout.",
                    exception);
        }
    }

    /**
     * Validates checkout details and starts mock payment.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when redirect fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        final HttpSession session =
                request.getSession(false);

        if (!isBuyer(session)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/buyer/login");
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        final String customerName =
                cleanRequired(
                        request.getParameter("name"),
                        MAX_NAME_LENGTH);

        final String customerPhone =
                cleanRequired(
                        request.getParameter("phone"),
                        PHONE_LENGTH);

        final String address =
                cleanRequired(
                        request.getParameter("address"),
                        MAX_ADDRESS_LENGTH);

        final String landmark =
                cleanOptional(
                        request.getParameter("landmark"),
                        MAX_LANDMARK_LENGTH);

        final String paymentMethod =
                request.getParameter("paymentMethod");

        if (customerName == null
                || customerPhone == null
                || address == null) {

            loadCheckoutPage(
                    request,
                    response,
                    "Please enter all required delivery details.");
            return;
        }

        if (!isValidPhone(customerPhone)) {
            loadCheckoutPage(
                    request,
                    response,
                    "Please enter a valid 10-digit phone number.");
            return;
        }

        if (!isValidPaymentMethod(paymentMethod)) {
            loadCheckoutPage(
                    request,
                    response,
                    "Please select a valid payment method.");
            return;
        }

        try {
            final CartDAO cartDAO =
                    new CartDAO(getDataSource(request));

            final List<CartItem> cartItems =
                    cartDAO.findByBuyer(user.getId());

            if (cartItems.isEmpty()) {
                response.sendRedirect(
                        request.getContextPath()
                                + CART_URL);
                return;
            }

            session.setAttribute(
                    "checkoutCustomerName",
                    customerName);

            session.setAttribute(
                    "checkoutCustomerPhone",
                    customerPhone);

            session.setAttribute(
                    "checkoutAddress",
                    address);

            session.setAttribute(
                    "checkoutLandmark",
                    landmark);

            session.setAttribute(
                    "checkoutPaymentMethod",
                    paymentMethod);

            response.sendRedirect(
                    request.getContextPath()
                            + PAYMENT_URL);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to prepare checkout.",
                    exception);
        }
    }

    /**
     * Reloads checkout with a validation error.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param error validation message
     * @throws ServletException when checkout reload fails
     * @throws IOException when forwarding fails
     */
    private void loadCheckoutPage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String error)
            throws ServletException, IOException {

        final HttpSession session =
                request.getSession(false);

        if (!isBuyer(session)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/buyer/login");
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        try {
            final CartDAO cartDAO =
                    new CartDAO(getDataSource(request));

            final List<CartItem> cartItems =
                    cartDAO.findByBuyer(user.getId());

            request.setAttribute(
                    "cartItems",
                    cartItems);

            request.setAttribute(
                    "error",
                    error);

            request.getRequestDispatcher(
                    CHECKOUT_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to reload checkout.",
                    exception);
        }
    }

    /**
     * Validates the selected payment method.
     *
     * @param paymentMethod submitted payment method
     * @return true when valid
     */
    private boolean isValidPaymentMethod(
            final String paymentMethod) {

        return "COD".equals(paymentMethod)
                || "UPI".equals(paymentMethod)
                || "CARD".equals(paymentMethod);
    }

    /**
     * Validates an Indian mobile number.
     *
     * @param phone submitted phone number
     * @return true when valid
     */
    private boolean isValidPhone(
            final String phone) {

        return phone != null
                && phone.matches("[6-9][0-9]{9}");
    }

    /**
     * Validates and trims a required value.
     *
     * @param value submitted value
     * @param maxLength maximum allowed length
     * @return trimmed value or null
     */
    private String cleanRequired(
            final String value,
            final int maxLength) {

        if (value == null || value.isBlank()) {
            return null;
        }

        final String trimmed =
                value.trim();

        if (trimmed.length() > maxLength) {
            return null;
        }

        return trimmed;
    }

    /**
     * Validates and trims an optional value.
     *
     * @param value submitted value
     * @param maxLength maximum allowed length
     * @return trimmed value or null
     */
    private String cleanOptional(
            final String value,
            final int maxLength) {

        if (value == null || value.isBlank()) {
            return null;
        }

        final String trimmed =
                value.trim();

        if (trimmed.length() > maxLength) {
            return null;
        }

        return trimmed;
    }

    /**
     * Checks whether the current session belongs to a buyer.
     *
     * @param session HTTP session
     * @return true when buyer is authenticated
     */
    private boolean isBuyer(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User user
                && User.ROLE_BUYER.equals(
                        user.getRole())
                && user.getId() != null;
    }

    /**
     * Gets the application data source.
     *
     * @param request HTTP request
     * @return application data source
     * @throws ServletException when unavailable
     */
    private DataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener
                                        .DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof DataSource dataSource)) {
            throw new ServletException(
                    "VR Mart database connection "
                            + "is unavailable.");
        }

        return dataSource;
    }
}
