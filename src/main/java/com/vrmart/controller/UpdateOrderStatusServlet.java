package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * Updates the status of a seller order.
 */
@WebServlet("/seller/orders/status")
public final class UpdateOrderStatusServlet
        extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Incoming orders URL. */
    private static final String ORDERS_URL =
            "/seller/orders";

    /**
     * Updates an order status.
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

        final HttpSession session =
                request.getSession(false);

        if (!isSeller(session)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/seller/login");
            return;
        }

        final String orderIdValue =
                request.getParameter("orderId");

        final String status =
                request.getParameter("status");

        final Long orderId =
                parseOrderId(orderIdValue);

        if (orderId == null
                || !isValidStatus(status)) {

            response.sendRedirect(
                    request.getContextPath()
                            + ORDERS_URL);
            return;
        }

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            orderDAO.updateStatus(
                    orderId,
                    status);

            response.sendRedirect(
                    request.getContextPath()
                            + ORDERS_URL);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to update order status.",
                    exception);
        }
    }

    /**
     * Parses an order identifier.
     *
     * @param value string value
     * @return parsed identifier or null
     */
    private Long parseOrderId(
            final String value) {

        if (value == null
                || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Checks whether the status is supported.
     *
     * @param status order status
     * @return true when valid
     */
    private boolean isValidStatus(
            final String status) {

        return "PENDING".equals(status)
                || "CONFIRMED".equals(status)
                || "PROCESSING".equals(status)
                || "SHIPPED".equals(status)
                || "DELIVERED".equals(status);
    }

    /**
     * Checks seller authentication.
     *
     * @param session current session
     * @return true when seller is logged in
     */
    private boolean isSeller(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User user
                && User.ROLE_SELLER.equals(
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
