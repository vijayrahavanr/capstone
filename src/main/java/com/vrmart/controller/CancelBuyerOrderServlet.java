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
 * Cancels an eligible buyer order.
 */
@WebServlet("/buyer/orders/cancel")
public final class CancelBuyerOrderServlet extends HttpServlet {
    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Buyer orders URL. */
    private static final String ORDERS_URL = "/buyer/orders";

    /** Session user attribute. */
    private static final String USER_ATTRIBUTE = "user";

    /**
     * Cancels a buyer order.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when redirect fails
     */
    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session = request.getSession(false);
        if (!isBuyer(session)) {
            response.sendRedirect(request.getContextPath()
                    + "/buyer/login");
            return;
        }

        final Long orderId = parseOrderId(request.getParameter("orderId"));
        if (orderId == null || orderId <= 0) {
            redirect(request, response, "Invalid order.");
            return;
        }

        final User user = (User) session.getAttribute(USER_ATTRIBUTE);
        try {
            final OrderDAO orderDAO = new OrderDAO(getDataSource(request));
            final boolean cancelled = orderDAO.cancelByBuyer(
                    user.getId(), orderId);
            final String message = cancelled
                    ? "Order cancelled successfully."
                    : "This order cannot be cancelled now.";
            redirect(request, response, message);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to cancel buyer order.", exception);
        }
    }

    /**
     * Redirects to the buyer orders page with a message.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param message result message
     * @throws IOException when redirect fails
     */
    private void redirect(final HttpServletRequest request,
                          final HttpServletResponse response,
                          final String message) throws IOException {
        response.sendRedirect(request.getContextPath() + ORDERS_URL
                + "?message=" + URLEncoder.encode(
                message, StandardCharsets.UTF_8));
    }

    /**
     * Parses an order identifier.
     *
     * @param value request value
     * @return parsed identifier or null
     */
    private Long parseOrderId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Checks buyer authentication.
     *
     * @param session current session
     * @return true when buyer is logged in
     */
    private boolean isBuyer(final HttpSession session) {
        if (session == null) {
            return false;
        }
        final Object userObject = session.getAttribute(USER_ATTRIBUTE);
        return userObject instanceof User user
                && User.ROLE_BUYER.equals(user.getRole())
                && user.getId() != null
                && user.getId() > 0;
    }

    /**
     * Returns the application data source.
     *
     * @param request HTTP request
     * @return database data source
     * @throws ServletException when unavailable
     */
    private DataSource getDataSource(final HttpServletRequest request)
            throws ServletException {
        final Object source = request.getServletContext().getAttribute(
                DatabaseListener.DATA_SOURCE_ATTRIBUTE);
        if (!(source instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }
        return (DataSource) source;
    }
}
