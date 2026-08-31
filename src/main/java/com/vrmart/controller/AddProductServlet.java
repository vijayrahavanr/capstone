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
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Handles seller product creation.
 */
@WebServlet("/seller/products")
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

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final Object userObject =
                session.getAttribute("user");

        if (!(userObject instanceof User user)
                || !User.ROLE_SELLER.equals(user.getRole())
                || user.getId() == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        try {
            final Product product = new Product();

            product.setSellerId(user.getId());

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

            final Object dataSourceObject =
                    getServletContext().getAttribute(
                            DatabaseListener.DATA_SOURCE_ATTRIBUTE);

            if (!(dataSourceObject
                    instanceof javax.sql.DataSource dataSource)) {

                throw new ServletException(
                        "VR Mart database connection is unavailable.");
            }

            final ProductDAO productDAO =
                    new ProductDAO(dataSource);

            productDAO.create(product);

            response.sendRedirect(
                    request.getContextPath() + "/products");

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
