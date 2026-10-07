package com.vrmart.controller;

import com.vrmart.dao.WishlistDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles buyer wishlist operations.
 */
@WebServlet("/buyer/wishlist")
public final class WishlistServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Wishlist page. */
    private static final String WISHLIST_PAGE =
            "/buyer/wishlist.jsp";

    /** Products URL. */
    private static final String PRODUCTS_URL =
            "/products";

    /** Wishlist URL. */
    private static final String WISHLIST_URL =
            "/buyer/wishlist";

    /** Login URL. */
    private static final String LOGIN_URL = "/login";

    /** Action parameter. */
    private static final String PARAM_ACTION = "action";

    /** Product ID parameter. */
    private static final String PARAM_PRODUCT_ID = "productId";

    /** Add action. */
    private static final String ACTION_ADD = "add";

    /** Remove action. */
    private static final String ACTION_REMOVE = "remove";

    /** Add success session attribute. */
    private static final String WISHLIST_SUCCESS =
            "wishlistSuccess";

    /** Remove success session attribute. */
    private static final String WISHLIST_REMOVE_SUCCESS =
            "wishlistRemoveSuccess";

    /** Invalid ID marker. */
    private static final long INVALID_ID = -1L;

    /**
     * Displays the buyer wishlist.
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

        if (!isBuyer(session)) {
            redirectToLogin(request, response);
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        try {
            final WishlistDAO wishlistDAO =
                    new WishlistDAO(getDataSource(request));

            request.setAttribute(
                    "wishlist",
                    wishlistDAO.findByBuyer(user.getId()));

            request.getRequestDispatcher(WISHLIST_PAGE)
                    .forward(request, response);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load wishlist.",
                    exception);
        }
    }

    /**
     * Processes wishlist add and remove operations.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when redirect fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session =
                request.getSession(false);

        if (!isBuyer(session)) {
            redirectToLogin(request, response);
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        final long productId =
                parseId(
                        request.getParameter(PARAM_PRODUCT_ID));

        final String action =
                clean(request.getParameter(PARAM_ACTION));

        if (productId == INVALID_ID || action.isEmpty()) {
            response.sendRedirect(
                    request.getContextPath() + PRODUCTS_URL);
            return;
        }

        try {
            final WishlistDAO wishlistDAO =
                    new WishlistDAO(getDataSource(request));

            if (ACTION_ADD.equals(action)) {
                handleAdd(
                        wishlistDAO,
                        session,
                        user.getId(),
                        productId);
                response.sendRedirect(
                        request.getContextPath() + PRODUCTS_URL);
                return;
            }

            if (ACTION_REMOVE.equals(action)) {
                wishlistDAO.remove(
                        user.getId(),
                        productId);

                session.setAttribute(
                        WISHLIST_REMOVE_SUCCESS,
                        "Removed from Wishlist.");

                response.sendRedirect(
                        request.getContextPath() + WISHLIST_URL);
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + PRODUCTS_URL);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to update wishlist.",
                    exception);
        }
    }

    /**
     * Adds a product to the wishlist.
     *
     * @param wishlistDAO wishlist data access object
     * @param session authenticated session
     * @param userId buyer identifier
     * @param productId product identifier
     * @throws Exception when wishlist access fails
     */
    private void handleAdd(
            final WishlistDAO wishlistDAO,
            final HttpSession session,
            final long userId,
            final long productId)
            throws Exception {

        if (wishlistDAO.exists(userId, productId)) {
            session.setAttribute(
                    WISHLIST_SUCCESS,
                    "Product is already in your wishlist.");
            return;
        }

        wishlistDAO.add(userId, productId);

        session.setAttribute(
                WISHLIST_SUCCESS,
                "Added to Wishlist.");
    }

    /**
     * Checks whether the current session belongs to a buyer.
     *
     * @param session current session
     * @return true when authenticated buyer
     */
    private boolean isBuyer(final HttpSession session) {
        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        if (!(userObject instanceof User)) {
            return false;
        }

        final User user = (User) userObject;

        return user.getId() != null
                && User.ROLE_BUYER.equals(user.getRole());
    }

    /**
     * Parses a positive identifier.
     *
     * @param value request value
     * @return parsed ID or invalid marker
     */
    private long parseId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return INVALID_ID;
        }

        try {
            final long id =
                    Long.parseLong(value.trim());

            return id > 0 ? id : INVALID_ID;
        } catch (NumberFormatException exception) {
            return INVALID_ID;
        }
    }

    /**
     * Cleans request input.
     *
     * @param value request value
     * @return trimmed value or empty string
     */
    private String clean(final String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Redirects unauthenticated users to login.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException when redirect fails
     */
    private void redirectToLogin(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + LOGIN_URL);
    }

    /**
     * Gets the application data source.
     *
     * @param request HTTP request
     * @return HikariCP data source
     * @throws ServletException when unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (HikariDataSource) source;
    }
}
