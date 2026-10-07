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

    /** Increase action. */
    private static final String ACTION_INCREASE = "increase";

    /** Decrease action. */
    private static final String ACTION_DECREASE = "decrease";

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
        final HttpSession session = request.getSession(false);
        if (!isBuyer(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }
        final User user = (User) session.getAttribute("user");
        try {
            final long productId =
                    parseId(request.getParameter("productId"));
            final String action =
                    request.getParameter("action");
            if (!ACTION_INCREASE.equals(action)
                    && !ACTION_DECREASE.equals(action)) {
                response.sendRedirect(
                        request.getContextPath() + CART_PAGE);
                return;
            }
            final CartDAO cartDAO =
                    new CartDAO(getDataSource(request));
            final List<CartItem> items =
                    cartDAO.findByBuyer(user.getId());
            CartItem selectedItem = null;
            for (final CartItem item : items) {
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
            final int newQuantity =
                    calculateQuantity(selectedItem, action);
            cartDAO.updateQuantity(
                    user.getId(), productId, newQuantity);
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
     * Calculates the new cart quantity.
     *
     * @param item selected cart item
     * @param action requested action
     * @return updated quantity
     */
    private int calculateQuantity(
            final CartItem item,
            final String action) {
        final int currentQuantity = item.getQuantity();
        if (ACTION_INCREASE.equals(action)) {
            if (item.getStockQty() <= 0) {
                return 1;
            }
            return Math.min(
                    currentQuantity + 1,
                    item.getStockQty());
        }
        return Math.max(currentQuantity - 1, 1);
    }

    /**
     * Parses a positive product identifier.
     *
     * @param value submitted identifier
     * @return validated identifier
     */
    private long parseId(final String value) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException("Missing product ID.");
        }
        final long id = Long.parseLong(value);
        if (id <= 0) {
            throw new NumberFormatException("Invalid product ID.");
        }
        return id;
    }

    /**
     * Checks whether the session belongs to a buyer.
     *
     * @param session HTTP session
     * @return true when buyer is authenticated
     */
    private boolean isBuyer(final HttpSession session) {
        if (session == null) {
            return false;
        }
        final Object userObject = session.getAttribute("user");
        return userObject instanceof User user
                && User.ROLE_BUYER.equals(user.getRole())
                && user.getId() != null;
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
                request.getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);
        if (!(dataSource instanceof DataSource dataSourceObject)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }
        return dataSourceObject;
    }
}
