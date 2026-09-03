package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.listener.DatabaseListener;
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
 * Displays orders belonging to the logged-in buyer.
 */
@WebServlet("/buyer/orders")
public final class BuyerOrdersServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Orders page path. */
    private static final String ORDERS_PAGE =
            "/buyer/orders.jsp";

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session =
                request.getSession(false);

        if (!isBuyer(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            final List<Order> orders =
                    orderDAO.findByBuyer(user.getId());

            request.setAttribute(
                    "orders",
                    orders);

            request.getRequestDispatcher(
                    ORDERS_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load buyer orders.",
                    exception);
        }
    }

    /**
     * Checks whether the session belongs to a buyer.
     *
     * @param session current session
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
                && User.ROLE_BUYER.equals(user.getRole())
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

        final Object dataSource =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener
                                        .DATA_SOURCE_ATTRIBUTE);

        if (!(dataSource instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (DataSource) dataSource;
    }
}
