package com.vrmart.controller;

import com.vrmart.dao.CartDAO;
import com.vrmart.dao.ProductDAO;
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
import java.sql.SQLException;
import java.util.List;

/**
 * Handles adding products to a buyer cart.
 */
@WebServlet("/buyer/cart/add")
public final class AddToCartServlet extends HttpServlet {
    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Default quantity when none is supplied. */
    private static final int DEFAULT_QUANTITY = 1;

    /** Maximum quantity allowed per request. */
    private static final int MAX_QUANTITY = 1000;

    /** Products page path. */
    private static final String PRODUCTS_PAGE = "/products";

    /** Cart page path. */
    private static final String CART_PAGE = "/buyer/cart";

    /**
     * Processes an add-to-cart request.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
     * @throws IOException when request processing fails
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
            final int quantity = getQuantity(request);
            final DataSource dataSource = getDataSource(request);
            final ProductDAO productDAO =
                    new ProductDAO(dataSource);
            final Integer stockQty =
                    productDAO.findStock(productId);
            if (stockQty == null) {
                throw new IllegalArgumentException(
                        "Product does not exist.");
            }
            if (stockQty <= 0) {
                throw new IllegalArgumentException(
                        "Product is out of stock.");
            }
            final CartDAO cartDAO = new CartDAO(dataSource);
            final List<CartItem> cartItems =
                    cartDAO.findByBuyer(user.getId());
            int existingQuantity = 0;
            for (final CartItem item : cartItems) {
                if (item.getProductId() == productId) {
                    existingQuantity = item.getQuantity();
                    break;
                }
            }
            if (existingQuantity > stockQty
                    || quantity > stockQty - existingQuantity) {
                throw new IllegalArgumentException(
                        "Only " + stockQty
                                + " item(s) are available in stock.");
            }
            cartDAO.addItem(
                    user.getId(), productId, quantity);
            response.sendRedirect(
                    request.getContextPath() + CART_PAGE);
        } catch (NumberFormatException exception) {
            setCartError(
                    request,
                    "Invalid product or quantity.");
            response.sendRedirect(
                    request.getContextPath() + PRODUCTS_PAGE);
        } catch (IllegalArgumentException exception) {
            setCartError(
                    request,
                    exception.getMessage());
            response.sendRedirect(
                    request.getContextPath() + PRODUCTS_PAGE);
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to add product to cart.", exception);
        }
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
     * Reads and validates the requested quantity.
     *
     * @param request HTTP request
     * @return validated quantity
     */
    private int getQuantity(final HttpServletRequest request) {
        final String value = request.getParameter("quantity");
        if (value == null || value.isBlank()) {
            return DEFAULT_QUANTITY;
        }
        final int quantity = Integer.parseInt(value);
        if (quantity <= 0 || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException(
                    "Quantity must be between 1 and "
                            + MAX_QUANTITY + ".");
        }
        return quantity;
    }

    /**
     * Stores a safe cart error message in the session.
     *
     * @param request HTTP request
     * @param message error message
     */
    private void setCartError(
            final HttpServletRequest request,
            final String message) {
        request.getSession(true).setAttribute(
                "cartError", message);
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
                request.getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);
        if (!(dataSource instanceof DataSource dataSourceObject)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }
        return dataSourceObject;
    }
}
