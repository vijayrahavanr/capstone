package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.SellerOrderItem;
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
 * Displays incoming orders containing the seller's products.
 */
@WebServlet("/seller/orders")
public final class SellerOrdersServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Seller orders JSP page. */
    private static final String ORDERS_PAGE =
            "/seller/orders.jsp";

    /**
     * Handles seller incoming order requests.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
     * @throws IOException when request processing fails
     */
    @Override
    protected void doGet(
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

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            final List<SellerOrderItem> orders =
                    orderDAO.findBySeller(user.getId());

            request.setAttribute(
                    "sellerOrders",
                    orders);

            request.getRequestDispatcher(
                    ORDERS_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load seller orders.",
                    exception);
        }
    }

    /**
     * Checks whether the session belongs to a seller.
     *
     * @param session current HTTP session
     * @return true when the logged-in user is a seller
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
     * Returns the application database data source.
     *
     * @param request HTTP request
     * @return configured data source
     * @throws ServletException when data source is unavailable
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
                    "VR Mart database connection "
                            + "is unavailable.");
        }

        return (DataSource) dataSource;
    }
}
