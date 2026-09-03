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
import java.util.List;

/**
 * Displays products belonging to the logged-in seller.
 */
@WebServlet("/seller/products")
public final class SellerProductsServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Seller products page. */
    private static final String PRODUCTS_PAGE =
            "/seller/products.jsp";

    /**
     * Displays seller products.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when redirect or forward fails
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
            final DataSource dataSource =
                    getDataSource(request);

            final ProductDAO productDAO =
                    new ProductDAO(dataSource);

            final List<Product> products =
                    productDAO.findBySeller(user.getId());

            request.setAttribute(
                    "products",
                    products);

            request.getRequestDispatcher(PRODUCTS_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load seller products.",
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
}
