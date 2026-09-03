package com.vrmart.controller;

import com.vrmart.dao.ProductDAO;
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
 * Handles seller product removal.
 */
@WebServlet("/seller/products/delete")
public final class RemoveProductServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * Removes a seller product.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when redirect fails
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

            final DataSource dataSource =
                    getDataSource(request);

            final ProductDAO productDAO =
                    new ProductDAO(dataSource);

            final boolean deleted =
                    productDAO.delete(
                            productId,
                            user.getId());

            if (deleted) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/seller/products"
                                + "?message=Product+removed"
                                + "+successfully");
            } else {
                response.sendRedirect(
                        request.getContextPath()
                                + "/seller/products"
                                + "?error=Product+not+found");
            }

        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/seller/products"
                            + "?error=Invalid+product");

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to remove product.",
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
