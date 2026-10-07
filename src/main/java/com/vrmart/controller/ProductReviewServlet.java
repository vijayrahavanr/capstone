package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.dao.ProductReviewDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.CartItem;
import com.vrmart.model.ProductReview;
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
 * Handles buyer product review and rating operations.
 */
@WebServlet("/buyer/product-review")
public final class ProductReviewServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Review page. */
    private static final String REVIEW_PAGE =
            "/buyer/product-review.jsp";

    /** Order details URL. */
    private static final String ORDER_DETAILS =
            "/buyer/order-details?orderId=";

    /** Login URL. */
    private static final String LOGIN_URL = "/login";

    /** Orders URL. */
    private static final String ORDERS_URL = "/buyer/orders";

    /** Order ID parameter. */
    private static final String PARAM_ORDER_ID = "orderId";

    /** Product ID parameter. */
    private static final String PARAM_PRODUCT_ID = "productId";

    /** Rating parameter. */
    private static final String PARAM_RATING = "rating";

    /** Review text parameter. */
    private static final String PARAM_REVIEW_TEXT = "reviewText";

    /** Minimum rating. */
    private static final int MIN_RATING = 1;

    /** Maximum rating. */
    private static final int MAX_RATING = 5;

    /** Minimum review length. */
    private static final int MIN_REVIEW_LENGTH = 3;

    /** Maximum review length. */
    private static final int MAX_REVIEW_LENGTH = 1000;

    /**
     * Displays the product review page.
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

        final Long orderId =
                parseId(request.getParameter(PARAM_ORDER_ID));

        final Long productId =
                parseId(request.getParameter(PARAM_PRODUCT_ID));

        if (orderId == null || productId == null) {
            redirectToOrders(request, response);
            return;
        }

        try {
            final DataSource dataSource =
                    getDataSource(request);
            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);
            final ProductReviewDAO reviewDAO =
                    new ProductReviewDAO(
                            request.getServletContext());

            if (!reviewDAO.isDeliveredOrderProduct(
                    orderId,
                    user.getId(),
                    productId)) {
                redirectToOrderDetails(
                        request,
                        response,
                        orderId);
                return;
            }

            final List<CartItem> orderItems =
                    orderDAO.findItemsByOrder(
                            orderId,
                            user.getId());

            final String productName =
                    findProductName(
                            orderItems,
                            productId);

            if (productName == null
                    || productName.trim().isEmpty()) {
                redirectToOrderDetails(
                        request,
                        response,
                        orderId);
                return;
            }

            final ProductReview review =
                    reviewDAO.findByBuyerAndProduct(
                            user.getId(),
                            productId);

            request.setAttribute("review", review);
            request.setAttribute(
                    "productName",
                    productName);

            request.getRequestDispatcher(REVIEW_PAGE)
                    .forward(request, response);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load product review.",
                    exception);
        }
    }

    /**
     * Creates or updates a product review.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when redirecting fails
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

        final Long orderId =
                parseId(request.getParameter(PARAM_ORDER_ID));

        final Long productId =
                parseId(request.getParameter(PARAM_PRODUCT_ID));

        final Integer rating =
                parseRating(request.getParameter(PARAM_RATING));

        final String reviewText =
                clean(request.getParameter(PARAM_REVIEW_TEXT));

        if (orderId == null || productId == null) {
            redirectToOrders(request, response);
            return;
        }

        if (rating == null
                || rating < MIN_RATING
                || rating > MAX_RATING) {
            showError(
                    request,
                    response,
                    orderId,
                    productId,
                    "Please select a rating from 1 to 5.");
            return;
        }

        if (reviewText.length() < MIN_REVIEW_LENGTH
                || reviewText.length() > MAX_REVIEW_LENGTH) {
            showError(
                    request,
                    response,
                    orderId,
                    productId,
                    "Review must contain between "
                            + MIN_REVIEW_LENGTH
                            + " and "
                            + MAX_REVIEW_LENGTH
                            + " characters.");
            return;
        }

        try {
            final ProductReviewDAO reviewDAO =
                    new ProductReviewDAO(
                            request.getServletContext());

            if (!reviewDAO.isDeliveredOrderProduct(
                    orderId,
                    user.getId(),
                    productId)) {
                showError(
                        request,
                        response,
                        orderId,
                        productId,
                        "You can review only products "
                                + "from delivered orders.");
                return;
            }

            final ProductReview existingReview =
                    reviewDAO.findByBuyerAndProduct(
                            user.getId(),
                            productId);

            if (existingReview == null) {
                createReview(
                        reviewDAO,
                        user,
                        orderId,
                        productId,
                        rating,
                        reviewText);
            } else {
                reviewDAO.updateReview(
                        existingReview.getId(),
                        user.getId(),
                        rating,
                        reviewText);
            }

            redirectToOrderDetails(
                    request,
                    response,
                    orderId);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to save product review.",
                    exception);
        }
    }

    /**
     * Creates a new product review.
     *
     * @param reviewDAO review data access object
     * @param user authenticated buyer
     * @param orderId order identifier
     * @param productId product identifier
     * @param rating submitted rating
     * @param reviewText submitted review
     * @throws Exception when review creation fails
     */
    private void createReview(
            final ProductReviewDAO reviewDAO,
            final User user,
            final long orderId,
            final long productId,
            final int rating,
            final String reviewText)
            throws Exception {

        final ProductReview newReview =
                new ProductReview();

        newReview.setProductId(productId);
        newReview.setBuyerId(user.getId());
        newReview.setOrderId(orderId);
        newReview.setRating(rating);
        newReview.setReviewText(reviewText);

        reviewDAO.createReview(newReview);
    }

    /**
     * Displays a review validation error.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param orderId order identifier
     * @param productId product identifier
     * @param message validation message
     * @throws ServletException when processing fails
     * @throws IOException when forwarding fails
     */
    private void showError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final long orderId,
            final long productId,
            final String message)
            throws ServletException, IOException {

        try {
            final DataSource dataSource =
                    getDataSource(request);
            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);
            final ProductReviewDAO reviewDAO =
                    new ProductReviewDAO(
                            request.getServletContext());
            final HttpSession session =
                    request.getSession(false);

            if (!isBuyer(session)) {
                redirectToLogin(request, response);
                return;
            }

            final User user =
                    (User) session.getAttribute("user");

            final List<CartItem> orderItems =
                    orderDAO.findItemsByOrder(
                            orderId,
                            user.getId());

            String productName = "Product";

            final String foundProductName =
                    findProductName(
                            orderItems,
                            productId);

            if (foundProductName != null
                    && !foundProductName.trim().isEmpty()) {
                productName = foundProductName;
            }

            final ProductReview review =
                    reviewDAO.findByBuyerAndProduct(
                            user.getId(),
                            productId);

            request.setAttribute("review", review);
            request.setAttribute(
                    "productName",
                    productName);
            request.setAttribute("error", message);

            request.getRequestDispatcher(REVIEW_PAGE)
                    .forward(request, response);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to display review error.",
                    exception);
        }
    }

    /**
     * Finds a product name in the order items.
     *
     * @param orderItems order items
     * @param productId product identifier
     * @return product name or null
     */
    private String findProductName(
            final List<CartItem> orderItems,
            final long productId) {

        if (orderItems == null) {
            return null;
        }

        for (final CartItem item : orderItems) {
            if (item.getProductId() == productId) {
                return item.getProductName();
            }
        }

        return null;
    }

    /**
     * Checks whether the session belongs to a buyer.
     *
     * @param session current HTTP session
     * @return true for a valid buyer session
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
     * @param value identifier text
     * @return parsed identifier or null
     */
    private Long parseId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            final long id =
                    Long.parseLong(value.trim());

            return id > 0 ? id : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Parses the submitted rating.
     *
     * @param value rating text
     * @return parsed rating or null
     */
    private Integer parseRating(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Cleans submitted review text.
     *
     * @param value submitted text
     * @return trimmed text
     */
    private String clean(final String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Gets the configured application data source.
     *
     * @param request HTTP request
     * @return configured data source
     * @throws ServletException when unavailable
     */
    private DataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (DataSource) source;
    }

    /**
     * Redirects the user to the login page.
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
     * Redirects the buyer to the orders page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException when redirect fails
     */
    private void redirectToOrders(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + ORDERS_URL);
    }

    /**
     * Redirects to the selected order details page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param orderId order identifier
     * @throws IOException when redirect fails
     */
    private void redirectToOrderDetails(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final long orderId)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                        + ORDER_DETAILS
                        + orderId);
    }
}
