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
 * Handles cart quantity increase and decrease.
 */
@WebServlet("/buyer/cart/update")
public final class UpdateCartServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Cart page URL. */
    private static final String CART_PAGE = "/buyer/cart";

    /**
     * Updates the quantity of a cart item.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doPost(
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

            final String action =
                    request.getParameter("action");

            if (!"increase".equals(action)
                    && !"decrease".equals(action)) {

                response.sendRedirect(
                        request.getContextPath() + CART_PAGE);
                return;
            }

            final DataSource dataSource =
                    getDataSource(request);

            final CartDAO cartDAO =
                    new CartDAO(dataSource);

            final List<CartItem> items =
                    cartDAO.findByBuyer(user.getId());

            CartItem selectedItem = null;

            for (CartItem item : items) {
                if (item.getProductId() == productId) {
                    selectedItem = item;
                    break;
                }
            }

            if (selectedItem == null) {
                response.sendRedirect(
                        request.getContextPath() + CART_PAGE);
                return;
            }

            int newQuantity =
                    selectedItem.getQuantity();

            if ("increase".equals(action)) {
                if (newQuantity < selectedItem.getStockQty()) {
                    newQuantity++;
                }
            } else if ("decrease".equals(action)) {
                if (newQuantity > 1) {
                    newQuantity--;
                }
            }

            cartDAO.updateQuantity(
                    user.getId(),
                    productId,
                    newQuantity);

            response.sendRedirect(
                    request.getContextPath() + CART_PAGE);

        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath() + CART_PAGE);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to update cart quantity.",
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
