package com.vrmart.controller;

import com.vrmart.dao.CartDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.CartItem;
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
 * Displays the current buyer shopping cart.
 */
@WebServlet("/buyer/cart")
public final class CartServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Cart page path. */
    private static final String CART_PAGE =
            "/buyer/cart.jsp";

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session =
                request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final Object userObject =
                session.getAttribute("user");

        if (!(userObject instanceof User user)
                || !User.ROLE_BUYER.equals(user.getRole())
                || user.getId() == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final CartDAO cartDAO =
                    new CartDAO(dataSource);

            final List<CartItem> items =
                    cartDAO.findByBuyer(user.getId());

            request.setAttribute("cartItems", items);

            request.getRequestDispatcher(CART_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {

            throw new ServletException(
                    "Unable to load shopping cart.",
                    exception);
        }
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
