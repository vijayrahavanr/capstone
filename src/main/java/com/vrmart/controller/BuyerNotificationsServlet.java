package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.dao.OrderServiceRequestDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.Order;
import com.vrmart.model.OrderServiceRequest;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays buyer notifications generated from order activity.
 */
@WebServlet("/buyer/notifications")
public final class BuyerNotificationsServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * Displays notifications for the logged-in buyer.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when loading notifications fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("user") instanceof User)) {
            response.sendRedirect(
                    request.getContextPath() + "/buyer/login");
            return;
        }

        final User user = (User) session.getAttribute("user");

        final Object dataSourceObject =
                getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(dataSourceObject instanceof DataSource)) {
            throw new ServletException(
                    "Database connection is not available.");
        }

        final DataSource dataSource =
                (DataSource) dataSourceObject;

        try {
            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            final OrderServiceRequestDAO requestDAO =
                    new OrderServiceRequestDAO(dataSource);

            final List<Order> orders =
                    orderDAO.findByBuyer(user.getId());

            final List<OrderServiceRequest> serviceRequests =
                    new ArrayList<>();

            for (Order order : orders) {
                serviceRequests.addAll(
                        requestDAO.findByOrder(
                                order.getId(),
                                user.getId()));
            }

            request.setAttribute("orders", orders);
            request.setAttribute(
                    "serviceRequests",
                    serviceRequests);

            request.getRequestDispatcher(
                    "/buyer/notifications.jsp")
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load notifications.",
                    exception);
        }
    }
}
