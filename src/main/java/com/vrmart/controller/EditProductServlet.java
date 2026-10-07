package com.vrmart.controller;

import com.vrmart.dao.ProductDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.Product;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Handles seller product modification.
 */
@WebServlet("/seller/products/edit")
public final class EditProductServlet extends HttpServlet {
    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Edit product page. */
    private static final String EDIT_PAGE = "/seller/edit-product.jsp";

    /** Seller products page. */
    private static final String PRODUCTS_PAGE = "/seller/products";

    /** Maximum product name length. */
    private static final int MAX_NAME_LENGTH = 150;

    /** Maximum description length. */
    private static final int MAX_DESCRIPTION_LENGTH = 2000;

    /** Maximum category length. */
    private static final int MAX_CATEGORY_LENGTH = 100;

    /** Maximum image URL length. */
    private static final int MAX_IMAGE_URL_LENGTH = 1000;

    /** Maximum allowed stock quantity. */
    private static final int MAX_STOCK_QUANTITY = 1000000;

    /**
     * Displays the edit product page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session = request.getSession(false);
        if (!isSeller(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/seller/login");
            return;
        }
        final User user = (User) session.getAttribute("user");
        try {
            final long productId = parseId(
                    request.getParameter("id"));
            final ProductDAO productDAO =
                    new ProductDAO(getDataSource(request));
            final Product product =
                    productDAO.findByIdAndSeller(
                            productId, user.getId());
            if (product == null) {
                response.sendRedirect(
                        request.getContextPath() + PRODUCTS_PAGE);
                return;
            }
            request.setAttribute("product", product);
            request.getRequestDispatcher(EDIT_PAGE)
                    .forward(request, response);
        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath() + PRODUCTS_PAGE);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load product.", exception);
        }
    }

    /**
     * Processes product modification.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        final HttpSession session = request.getSession(false);
        if (!isSeller(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/seller/login");
            return;
        }
        final User user = (User) session.getAttribute("user");
        try {
            final long productId =
                    parseId(request.getParameter("id"));
            final String name = required(
                    request.getParameter("name"),
                    "Product name is required.",
                    MAX_NAME_LENGTH);
            final String description = optional(
                    request.getParameter("description"),
                    MAX_DESCRIPTION_LENGTH,
                    "Description");
            final BigDecimal price =
                    parsePrice(request.getParameter("price"));
            final int stockQty =
                    parseStock(request.getParameter("stockQty"));
            final String category = required(
                    request.getParameter("category"),
                    "Category is required.",
                    MAX_CATEGORY_LENGTH);
            final String imageUrl =
                    validateImageUrl(request.getParameter("imageUrl"));
            final ProductDAO productDAO =
                    new ProductDAO(getDataSource(request));
            final Product product =
                    productDAO.findByIdAndSeller(
                            productId, user.getId());
            if (product == null) {
                response.sendRedirect(
                        request.getContextPath() + PRODUCTS_PAGE);
                return;
            }
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQty(stockQty);
            product.setCategory(category);
            product.setImageUrl(imageUrl);
            if (!productDAO.update(product)) {
                request.setAttribute(
                        "error", "Unable to update product.");
                request.setAttribute("product", product);
                request.getRequestDispatcher(EDIT_PAGE)
                        .forward(request, response);
                return;
            }
            response.sendRedirect(
                    request.getContextPath() + PRODUCTS_PAGE);
        } catch (NumberFormatException exception) {
            request.setAttribute(
                    "error",
                    "Enter valid product ID, price and stock quantity.");
            request.getRequestDispatcher(EDIT_PAGE)
                    .forward(request, response);
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            request.getRequestDispatcher(EDIT_PAGE)
                    .forward(request, response);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to update product.", exception);
        }
    }

    /**
     * Parses a positive entity identifier.
     *
     * @param value submitted identifier
     * @return validated identifier
     */
    private long parseId(final String value) {
        if (value == null || value.isBlank()) {
            throw new NumberFormatException("Missing identifier.");
        }
        final long id = Long.parseLong(value);
        if (id <= 0) {
            throw new NumberFormatException("Invalid identifier.");
        }
        return id;
    }

    /**
     * Parses and validates a product price.
     *
     * @param value submitted price
     * @return validated price
     */
    private BigDecimal parsePrice(final String value) {
        final String price = required(
                value, "Price is required.", Integer.MAX_VALUE);
        final BigDecimal amount = new BigDecimal(price);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero.");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Price can contain at most two decimal places.");
        }
        return amount;
    }

    /**
     * Parses and validates stock quantity.
     *
     * @param value submitted stock quantity
     * @return validated stock quantity
     */
    private int parseStock(final String value) {
        final String stock = required(
                value,
                "Stock quantity is required.",
                Integer.MAX_VALUE);
        final int quantity = Integer.parseInt(stock);
        if (quantity < 0 || quantity > MAX_STOCK_QUANTITY) {
            throw new IllegalArgumentException(
                    "Stock quantity must be between 0 and "
                            + MAX_STOCK_QUANTITY + ".");
        }
        return quantity;
    }

    /**
     * Validates an optional image URL.
     *
     * @param value submitted image URL
     * @return validated image URL
     */
    private String validateImageUrl(final String value) {
        final String imageUrl = optional(
                value, MAX_IMAGE_URL_LENGTH, "Image URL");
        if (imageUrl == null) {
            return null;
        }
        if (!imageUrl.matches("(?i)https?://[^\\s]+")) {
            throw new IllegalArgumentException(
                    "Image URL must start with http:// or https://.");
        }
        return imageUrl;
    }

    /**
     * Checks whether the current session belongs to a seller.
     *
     * @param session HTTP session
     * @return true when a valid seller is logged in
     */
    private boolean isSeller(final HttpSession session) {
        if (session == null) {
            return false;
        }
        final Object userObject = session.getAttribute("user");
        return userObject instanceof User user
                && User.ROLE_SELLER.equals(user.getRole())
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
        final Object dataSourceObject =
                request.getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);
        if (!(dataSourceObject instanceof DataSource dataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }
        return dataSource;
    }

    /**
     * Validates a required form value.
     *
     * @param value submitted value
     * @param message error message
     * @param maxLength maximum length
     * @return trimmed value
     */
    private String required(
            final String value,
            final String message,
            final int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        final String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(
                    "Submitted value is too long.");
        }
        return trimmed;
    }

    /**
     * Validates an optional form value.
     *
     * @param value submitted value
     * @param maxLength maximum length
     * @param fieldName field name
     * @return trimmed value or null
     */
    private String optional(
            final String value,
            final int maxLength,
            final String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        final String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " is too long.");
        }
        return trimmed;
    }
}
