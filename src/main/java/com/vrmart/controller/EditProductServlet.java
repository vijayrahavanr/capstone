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
    private static final String EDIT_PAGE =
            "/seller/edit-product.jsp";

    /** Seller products page. */
    private static final String PRODUCTS_PAGE =
            "/seller/products";

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
            final long productId =
                    Long.parseLong(
                            request.getParameter("id"));

            final ProductDAO productDAO =
                    new ProductDAO(
                            getDataSource(request));

            final Product product =
                    productDAO.findByIdAndSeller(
                            productId,
                            user.getId());

            if (product == null) {
                response.sendRedirect(
                        request.getContextPath()
                                + PRODUCTS_PAGE);
                return;
            }

            request.setAttribute(
                    "product",
                    product);

            request.getRequestDispatcher(EDIT_PAGE)
                    .forward(request, response);

        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath()
                            + PRODUCTS_PAGE);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load product.",
                    exception);
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
            final long productId =
                    Long.parseLong(
                            request.getParameter("id"));

            final String name =
                    required(
                            request.getParameter("name"),
                            "Product name is required.");

            final String description =
                    request.getParameter("description");

            final BigDecimal price =
                    new BigDecimal(
                            required(
                                    request.getParameter("price"),
                                    "Price is required."));

            final int stockQty =
                    Integer.parseInt(
                            required(
                                    request.getParameter("stockQty"),
                                    "Stock quantity is required."));

            final String category =
                    required(
                            request.getParameter("category"),
                            "Category is required.");

            final String imageUrl =
                    request.getParameter("imageUrl");

            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Price cannot be negative.");
            }

            if (stockQty < 0) {
                throw new IllegalArgumentException(
                        "Stock quantity cannot be negative.");
            }

            final ProductDAO productDAO =
                    new ProductDAO(
                            getDataSource(request));

            final Product product =
                    productDAO.findByIdAndSeller(
                            productId,
                            user.getId());

            if (product == null) {
                response.sendRedirect(
                        request.getContextPath()
                                + PRODUCTS_PAGE);
                return;
            }

            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQty(stockQty);
            product.setCategory(category);
            product.setImageUrl(imageUrl);

            final boolean updated =
                    productDAO.update(product);

            if (!updated) {
                request.setAttribute(
                        "error",
                        "Unable to update product.");

                request.setAttribute(
                        "product",
                        product);

                request.getRequestDispatcher(EDIT_PAGE)
                        .forward(request, response);
                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                            + PRODUCTS_PAGE);

        } catch (NumberFormatException exception) {
            request.setAttribute(
                    "error",
                    "Enter valid product ID, price "
                            + "and stock quantity.");

            request.getRequestDispatcher(EDIT_PAGE)
                    .forward(request, response);

        } catch (IllegalArgumentException exception) {
            request.setAttribute(
                    "error",
                    exception.getMessage());

            request.getRequestDispatcher(EDIT_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to update product.",
                    exception);
        }
    }

    /**
     * Checks whether the current session belongs to a seller.
     *
     * @param session HTTP session
     * @return true when a valid seller is logged in
     */
    private boolean isSeller(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

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
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener
                                        .DATA_SOURCE_ATTRIBUTE);

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
     * @return trimmed value
     */
    private String required(
            final String value,
            final String message) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }
}
