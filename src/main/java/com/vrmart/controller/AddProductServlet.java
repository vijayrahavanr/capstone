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
 * Handles seller product creation.
 */
@WebServlet("/seller/products/add")
public final class AddProductServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Seller product form page. */
    private static final String FORM_PAGE =
            "/seller/add-product.jsp";

    /**
     * Processes the product creation request.
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

            final Product product =
                    createProduct(request, user);

            final ProductDAO productDAO =
                    new ProductDAO(
                            getDataSource(request));

            productDAO.create(product);

            /*
             * After successful creation,
             * open SELLER products page.
             */
            response.sendRedirect(
                    request.getContextPath()
                            + "/seller/products");

        } catch (NumberFormatException exception) {

            request.setAttribute(
                    "error",
                    "Enter valid price and stock quantity.");

            request.getRequestDispatcher(FORM_PAGE)
                    .forward(request, response);

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage());

            request.getRequestDispatcher(FORM_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {

            throw new ServletException(
                    "Unable to create product.",
                    exception);
        }
    }

    /**
     * Creates a product from submitted values.
     *
     * @param request HTTP request
     * @param user logged-in seller
     * @return product
     */
    private Product createProduct(
            final HttpServletRequest request,
            final User user) {

        final Product product =
                new Product();

        product.setSellerId(
                user.getId());

        product.setName(
                required(
                        request.getParameter("name"),
                        "Product name is required."));

        product.setDescription(
                request.getParameter("description"));

        product.setPrice(
                new BigDecimal(
                        required(
                                request.getParameter("price"),
                                "Price is required.")));

        product.setStockQty(
                Integer.parseInt(
                        required(
                                request.getParameter("stockQty"),
                                "Stock quantity is required.")));

        product.setCategory(
                required(
                        request.getParameter("category"),
                        "Category is required."));

        product.setImageUrl(
                request.getParameter("imageUrl"));

        if (product.getPrice()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Price cannot be negative.");
        }

        if (product.getStockQty() < 0) {

            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative.");
        }

        return product;
    }

    /**
     * Checks whether the session belongs to a seller.
     *
     * @param session HTTP session
     * @return true when seller is authenticated
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
     * @return data source
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

        if (!(dataSourceObject
                instanceof DataSource dataSource)) {

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
