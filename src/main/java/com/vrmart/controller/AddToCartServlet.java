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

            final int quantity =
                    getQuantity(request);

            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero.");
            }

            final DataSource dataSource =
                    getDataSource(request);

            final ProductDAO productDAO =
                    new ProductDAO(dataSource);

            final Integer stockQty =
                    productDAO.findStock(productId);

            if (stockQty == null) {
                throw new IllegalArgumentException(
                        "Product does not exist.");
            }

            final CartDAO cartDAO =
                    new CartDAO(dataSource);

            final List<CartItem> cartItems =
                    cartDAO.findByBuyer(user.getId());

            int existingQuantity = 0;

            for (CartItem item : cartItems) {
                if (item.getProductId() == productId) {
                    existingQuantity =
                            item.getQuantity();
                    break;
                }
            }

            if (existingQuantity + quantity > stockQty) {
                throw new IllegalArgumentException(
                        "Only " + stockQty
                                + " item(s) are available in stock.");
            }

            cartDAO.addItem(
                    user.getId(),
                    productId,
                    quantity);

            response.sendRedirect(
                    request.getContextPath() + CART_PAGE);

        } catch (NumberFormatException exception) {

            request.getSession().setAttribute(
                    "cartError",
                    "Invalid product or quantity.");

            response.sendRedirect(
                    request.getContextPath()
                            + PRODUCTS_PAGE);

        } catch (IllegalArgumentException exception) {

            request.getSession().setAttribute(
                    "cartError",
                    exception.getMessage());

            response.sendRedirect(
                    request.getContextPath()
                            + PRODUCTS_PAGE);

        } catch (SQLException exception) {

            throw new ServletException(
                    "Unable to add product to cart.",
                    exception);
        }
    }

    /**
     * Reads the requested quantity.
     *
     * @param request HTTP request
     * @return requested quantity
     */
    private int getQuantity(
            final HttpServletRequest request) {

        final String value =
                request.getParameter("quantity");

        if (value == null || value.isBlank()) {
            return DEFAULT_QUANTITY;
        }

        return Integer.parseInt(value);
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
