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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles seller order status updates.
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

        final User user =
                (User) session.getAttribute("user");

        final Long orderId =
                parseOrderId(
                        request.getParameter("orderId"));

        final String status =
                request.getParameter("status");

        if (orderId == null
                || !isValidStatus(status)) {

            redirect(
                    request,
                    response,
                    "Invalid order status.",
                    true);
            return;
        }

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            final boolean updated =
                    orderDAO.updateStatus(
                            orderId,
                            user.getId(),
                            status);

            if (updated) {
                redirect(
                        request,
                        response,
                        "Order status updated successfully.",
                        false);
            } else {
                redirect(
                        request,
                        response,
                        "Order status cannot be updated now.",
                        true);
            }

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to update order status.",
                    exception);
        }
    }

    /**
     * Redirects to the seller orders page with a result message.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param message result message
     * @param error whether the message is an error
     * @throws IOException when redirect fails
     */
    private void redirect(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String message,
            final boolean error)
            throws IOException {

        final String parameter =
                error ? "error" : "message";

        response.sendRedirect(
                request.getContextPath()
                        + ORDERS_URL
                        + "?"
                        + parameter
                        + "="
                        + URLEncoder.encode(
                                message,
                                StandardCharsets.UTF_8));
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
     * Checks supported seller workflow statuses.
     *
     * @param status order status
     * @return true when valid
     */
    private boolean isValidStatus(
            final String status) {

        return OrderDAO.STATUS_APPROVED.equals(status)
                || OrderDAO.STATUS_SHIPPED.equals(status)
                || OrderDAO.STATUS_DELIVERED.equals(status);
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
     * @throws ServletException when data source is unavailable
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
