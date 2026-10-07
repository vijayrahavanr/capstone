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
 * Handles mock payment confirmation before creating a buyer order.
 */
@WebServlet("/buyer/payment-confirmation")
public final class PaymentConfirmationServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Payment confirmation page. */
    private static final String PAYMENT_PAGE =
            "/buyer/payment-confirmation.jsp";

    /** Checkout URL. */
    private static final String CHECKOUT_URL =
            "/buyer/checkout";

    /** Cart URL. */
    private static final String CART_URL =
            "/buyer/cart";

    /** Order success page. */
    private static final String SUCCESS_PAGE =
            "/buyer/order-success.jsp";

    /**
     * Displays the mock payment confirmation page.
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

        final String customerName =
                (String) session.getAttribute(
                        "checkoutCustomerName");

        final String customerPhone =
                (String) session.getAttribute(
                        "checkoutCustomerPhone");

        final String address =
                (String) session.getAttribute(
                        "checkoutAddress");

        final String landmark =
                (String) session.getAttribute(
                        "checkoutLandmark");

        final String paymentMethod =
                (String) session.getAttribute(
                        "checkoutPaymentMethod");

        if (customerName == null
                || customerPhone == null
                || address == null
                || paymentMethod == null) {

            response.sendRedirect(
                    request.getContextPath()
                            + CHECKOUT_URL);
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
                clearCheckoutData(session);

                response.sendRedirect(
                        request.getContextPath()
                                + CART_URL);
                return;
            }

            request.setAttribute(
                    "cartItems",
                    cartItems);

            request.setAttribute(
                    "customerName",
                    customerName);

            request.setAttribute(
                    "customerPhone",
                    customerPhone);

            request.setAttribute(
                    "address",
                    address);

            request.setAttribute(
                    "landmark",
                    landmark);

            request.setAttribute(
                    "paymentMethod",
                    paymentMethod);

            request.getRequestDispatcher(
                    PAYMENT_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load payment confirmation.",
                    exception);
        }
    }

    /**
     * Confirms the mock payment and creates the order.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when order creation fails
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
                (String) session.getAttribute(
                        "checkoutCustomerName");

        final String customerPhone =
                (String) session.getAttribute(
                        "checkoutCustomerPhone");

        final String address =
                (String) session.getAttribute(
                        "checkoutAddress");

        final String landmark =
                (String) session.getAttribute(
                        "checkoutLandmark");

        final String paymentMethod =
                (String) session.getAttribute(
                        "checkoutPaymentMethod");

        if (customerName == null
                || customerPhone == null
                || address == null
                || paymentMethod == null) {

            response.sendRedirect(
                    request.getContextPath()
                            + CHECKOUT_URL);
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
                clearCheckoutData(session);

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
                            customerName,
                            customerPhone,
                            address,
                            landmark,
                            paymentMethod);

            session.setAttribute(
                    "lastOrder",
                    order);

            clearCheckoutData(session);

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
     * Clears temporary checkout data.
     *
     * @param session HTTP session
     */
    private void clearCheckoutData(
            final HttpSession session) {

        session.removeAttribute(
                "checkoutCustomerName");

        session.removeAttribute(
                "checkoutCustomerPhone");

        session.removeAttribute(
                "checkoutAddress");

        session.removeAttribute(
                "checkoutLandmark");

        session.removeAttribute(
                "checkoutPaymentMethod");
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
