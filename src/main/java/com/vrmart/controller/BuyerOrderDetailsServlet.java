package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.dao.OrderServiceRequestDAO;
import com.vrmart.dao.ProductReviewDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.CartItem;
import com.vrmart.model.Order;
import com.vrmart.model.OrderServiceRequest;
import com.vrmart.model.ProductReview;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays details of a buyer's selected order.
 */
@WebServlet("/buyer/order-details")
public final class BuyerOrderDetailsServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Order details JSP page. */
    private static final String DETAILS_PAGE =
            "/buyer/order-details.jsp";

    /** Buyer orders URL. */
    private static final String ORDERS_URL = "/buyer/orders";

    @Override
    protected void doGet(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session = request.getSession(false);

        if (!isBuyer(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final User user = (User) session.getAttribute("user");
        final String orderIdParameter =
                request.getParameter("orderId");

        if (orderIdParameter == null
                || orderIdParameter.trim().isEmpty()) {
            response.sendRedirect(
                    request.getContextPath() + ORDERS_URL);
            return;
        }

        final long orderId;

        try {
            orderId = Long.parseLong(orderIdParameter.trim());
        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath() + ORDERS_URL);
            return;
        }

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            final OrderServiceRequestDAO serviceRequestDAO =
                    new OrderServiceRequestDAO(dataSource);

            final ProductReviewDAO reviewDAO =
                    new ProductReviewDAO(
                            request.getServletContext());

            final List<Order> buyerOrders =
                    orderDAO.findByBuyer(user.getId());

            Order selectedOrder = null;

            for (final Order order : buyerOrders) {
                if (order.getId() == orderId) {
                    selectedOrder = order;
                    break;
                }
            }

            if (selectedOrder == null) {
                response.sendRedirect(
                        request.getContextPath() + ORDERS_URL);
                return;
            }

            final List<CartItem> orderItems =
                    orderDAO.findItemsByOrder(
                            orderId,
                            user.getId());

            final List<OrderServiceRequest> serviceRequests =
                    serviceRequestDAO.findByOrder(
                            orderId,
                            user.getId());

            final Map<Long, ProductReview> productReviews =
                    new HashMap<>();

            for (final CartItem item : orderItems) {
                final ProductReview review =
                        reviewDAO.findByBuyerAndProduct(
                                user.getId(),
                                item.getProductId());

                if (review != null) {
                    productReviews.put(
                            item.getProductId(),
                            review);
                }
            }

            request.setAttribute("order", selectedOrder);
            request.setAttribute("orderItems", orderItems);
            request.setAttribute(
                    "serviceRequests",
                    serviceRequests);
            request.setAttribute(
                    "productReviews",
                    productReviews);

            request.getRequestDispatcher(DETAILS_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load order details.",
                    exception);
        }
    }

    /**
     * Checks whether the current session belongs to a buyer.
     *
     * @param session current HTTP session
     * @return true when the session contains a buyer
     */
    private boolean isBuyer(final HttpSession session) {
        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User
                && User.ROLE_BUYER.equals(
                        ((User) userObject).getRole());
    }

    /**
     * Gets the configured application data source.
     *
     * @param request current HTTP request
     * @return configured data source
     * @throws ServletException when data source is unavailable
     */
    private DataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (DataSource) source;
    }
}
