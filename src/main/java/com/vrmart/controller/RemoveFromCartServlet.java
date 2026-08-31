package com.vrmart.controller;

import com.vrmart.dao.CartDAO;
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
 * Removes a product from the buyer cart.
 */
@WebServlet("/buyer/cart/remove")
public final class RemoveFromCartServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Cart page URL. */
    private static final String CART_PAGE = "/buyer/cart";

    /**
     * Removes the requested product from the buyer cart.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an input or output error occurs
     */
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
            final long productId =
                    Long.parseLong(
                            request.getParameter("productId"));

            final DataSource dataSource =
                    getDataSource(request);

            final CartDAO cartDAO =
                    new CartDAO(dataSource);

            cartDAO.removeItem(
                    user.getId(),
                    productId);

            response.sendRedirect(
                    request.getContextPath() + CART_PAGE);

        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath() + CART_PAGE);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to remove product from cart.",
                    exception);
        }
    }

    /**
     * Gets the application data source.
     *
     * @param request HTTP request
     * @return configured application data source
     * @throws ServletException if the data source is unavailable
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
