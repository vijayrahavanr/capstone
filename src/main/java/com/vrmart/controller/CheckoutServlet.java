package com.vrmart.controller;

import com.vrmart.dao.CartDAO;
import com.vrmart.dao.OrderDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.CartItem;
import com.vrmart.model.Order;
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
 * Handles buyer checkout and order creation.
 */
@WebServlet("/buyer/checkout")
public final class CheckoutServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Checkout JSP page. */
    private static final String CHECKOUT_PAGE =
            "/buyer/checkout.jsp";

    /** Buyer cart URL. */
    private static final String CART_URL =
            "/buyer/cart";

    /** Order success page. */
    private static final String SUCCESS_PAGE =
            "/buyer/order-success.jsp";

    /**
     * Displays the checkout page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when request processing fails
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
            final DataSource dataSource =
                    getDataSource(request);

            final CartDAO cartDAO =
                    new CartDAO(dataSource);

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
     * Creates an order from the checkout form.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when request processing fails
     */
    @Override
    protected void doPost(
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

        final String address =
                request.getParameter("address");

        final String landmark =
                request.getParameter("landmark");

        final String paymentMethod =
                request.getParameter("paymentMethod");

        if (address == null
                || address.trim().isEmpty()) {

            loadCheckoutPage(
                    request,
                    response,
                    "Delivery address is required.");
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
            final DataSource dataSource =
                    getDataSource(request);

            final CartDAO cartDAO =
                    new CartDAO(dataSource);

            final List<CartItem> cartItems =
                    cartDAO.findByBuyer(user.getId());

            if (cartItems.isEmpty()) {
                response.sendRedirect(
                        request.getContextPath()
                                + CART_URL);
                return;
            }

            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            final Order order =
                    orderDAO.createOrder(
                            user.getId(),
                            cartItems,
                            address.trim(),
                            cleanLandmark(landmark),
                            paymentMethod);

            session.setAttribute(
                    "lastOrder",
                    order);

            response.sendRedirect(
                    request.getContextPath()
                            + SUCCESS_PAGE);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to create order.",
                    exception);
        }
    }

    /**
     * Loads checkout page with an error.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param error error message
     * @throws ServletException when forwarding fails
     * @throws IOException when forwarding fails
     */
    private void loadCheckoutPage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String error)
            throws ServletException, IOException {

        final HttpSession session =
                request.getSession(false);

        final User user =
                (User) session.getAttribute("user");

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final CartDAO cartDAO =
                    new CartDAO(dataSource);

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
     * @param paymentMethod selected payment method
     * @return true when valid
     */
    private boolean isValidPaymentMethod(
            final String paymentMethod) {

        return "COD".equals(paymentMethod)
                || "UPI".equals(paymentMethod)
                || "CARD".equals(paymentMethod);
    }

    /**
     * Cleans an optional landmark.
     *
     * @param landmark landmark value
     * @return cleaned landmark
     */
    private String cleanLandmark(
            final String landmark) {

        if (landmark == null
                || landmark.trim().isEmpty()) {
            return null;
        }

        return landmark.trim();
    }

    /**
     * Checks buyer authentication.
     *
     * @param session current session
     * @return true when buyer is logged in
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
     * Returns the application data source.
     *
     * @param request HTTP request
     * @return database data source
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

        if (!(source instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection "
                            + "is unavailable.");
        }

        return (DataSource) source;
    }
}
