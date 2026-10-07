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
 * Handles authenticated buyer and seller order cancellation.
 */
@WebServlet("/orders/cancel")
public final class CancelOrderServlet extends HttpServlet {
    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Buyer orders URL. */
    private static final String BUYER_ORDERS_URL = "/buyer/orders";

    /** Seller orders URL. */
    private static final String SELLER_ORDERS_URL = "/seller/orders";

    /** Login URL. */
    private static final String LOGIN_URL = "/login";

    /** Session user attribute. */
    private static final String USER_ATTRIBUTE = "user";

    /**
     * Cancels an order for the authenticated buyer or seller.
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
        if (session == null) {
            redirect(request, response, LOGIN_URL);
            return;
        }

        final Object userObject = session.getAttribute(USER_ATTRIBUTE);
        if (!(userObject instanceof User user)
                || user.getId() == null
                || user.getId() <= 0) {
            redirect(request, response, LOGIN_URL);
            return;
        }

        final Long orderId = parseOrderId(
                request.getParameter("orderId"));
        if (orderId == null || orderId <= 0) {
            redirectByRole(request, response, user);
            return;
        }

        if (!User.ROLE_BUYER.equals(user.getRole())
                && !User.ROLE_SELLER.equals(user.getRole())) {
            redirect(request, response, LOGIN_URL);
            return;
        }

        try {
            final OrderDAO orderDAO = new OrderDAO(getDataSource(request));
            if (User.ROLE_BUYER.equals(user.getRole())) {
                orderDAO.cancelByBuyer(orderId, user.getId());
                redirect(request, response, BUYER_ORDERS_URL);
            } else {
                orderDAO.cancelBySeller(orderId, user.getId());
                redirect(request, response, SELLER_ORDERS_URL);
            }
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to cancel order.", exception);
        }
    }

    /**
     * Redirects according to the authenticated role.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param user authenticated user
     * @throws IOException when redirect fails
     */
    private void redirectByRole(final HttpServletRequest request,
                                final HttpServletResponse response,
                                final User user) throws IOException {
        if (User.ROLE_SELLER.equals(user.getRole())) {
            redirect(request, response, SELLER_ORDERS_URL);
        } else if (User.ROLE_BUYER.equals(user.getRole())) {
            redirect(request, response, BUYER_ORDERS_URL);
        } else {
            redirect(request, response, LOGIN_URL);
        }
    }

    /**
     * Redirects to an application path.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param path application path
     * @throws IOException when redirect fails
     */
    private void redirect(final HttpServletRequest request,
                          final HttpServletResponse response,
                          final String path) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
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
     * Returns the application data source.
     *
     * @param request HTTP request
     * @return application data source
     * @throws ServletException when unavailable
     */
    private DataSource getDataSource(
            final HttpServletRequest request) throws ServletException {
        final Object source = request.getServletContext().getAttribute(
                DatabaseListener.DATA_SOURCE_ATTRIBUTE);
        if (!(source instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }
        return (DataSource) source;
    }
}
