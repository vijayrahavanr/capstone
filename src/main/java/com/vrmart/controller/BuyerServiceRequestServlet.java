package com.vrmart.controller;

import com.vrmart.dao.OrderServiceRequestDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.OrderServiceRequest;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles buyer return, replacement and help requests.
 */
@WebServlet("/buyer/service-request")
public final class BuyerServiceRequestServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Order details redirect URL. */
    private static final String ORDER_DETAILS_URL =
            "/buyer/order-details?orderId=";

    /** Return request type. */
    private static final String TYPE_RETURN = "RETURN";

    /** Replacement request type. */
    private static final String TYPE_REPLACEMENT =
            "REPLACEMENT";

    /** Help request type. */
    private static final String TYPE_NEED_HELP =
            "NEED_HELP";

    /** Delivered order status. */
    private static final String STATUS_DELIVERED =
            "DELIVERED";

    /** Cancelled order status. */
    private static final String STATUS_CANCELLED =
            "CANCELLED";

    /**
     * Handles service request submission.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
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
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        final String orderIdParameter =
                clean(request.getParameter("orderId"));

        final String productIdParameter =
                clean(request.getParameter("productId"));

        final String requestType =
                clean(request.getParameter("requestType"));

        final String reason =
                clean(request.getParameter("reason"));

        final long orderId;
        final long productId;

        try {
            orderId = Long.parseLong(orderIdParameter);
            productId = Long.parseLong(productIdParameter);
        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/buyer/orders");
            return;
        }

        try {
            final DataSource dataSource =
                    getDataSource(request);

            final OrderServiceRequestDAO requestDAO =
                    new OrderServiceRequestDAO(dataSource);

            final String orderStatus =
                    requestDAO.findOrderStatus(
                            orderId,
                            user.getId());

            if (orderStatus == null) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/buyer/orders");
                return;
            }

            final boolean productBelongs =
                    requestDAO.isProductInBuyerOrder(
                            orderId,
                            productId,
                            user.getId());

            if (!productBelongs) {
                redirectWithMessage(
                        request,
                        response,
                        orderId,
                        "Invalid product selected.");
                return;
            }

            if (!isValidType(requestType)
                    || reason.isEmpty()) {

                redirectWithMessage(
                        request,
                        response,
                        orderId,
                        "Please select a valid request "
                                + "and reason.");
                return;
            }

            if (TYPE_RETURN.equals(requestType)
                    || TYPE_REPLACEMENT.equals(requestType)) {

                if (!STATUS_DELIVERED.equals(orderStatus)) {
                    redirectWithMessage(
                            request,
                            response,
                            orderId,
                            "Return or replacement is "
                                    + "available only after delivery.");
                    return;
                }
            }

            if (TYPE_NEED_HELP.equals(requestType)
                    && STATUS_CANCELLED.equals(orderStatus)) {

                redirectWithMessage(
                        request,
                        response,
                        orderId,
                        "Help requests are unavailable "
                                + "for cancelled orders.");
                return;
            }

            final OrderServiceRequest serviceRequest =
                    new OrderServiceRequest();

            serviceRequest.setOrderId(orderId);
            serviceRequest.setBuyerId(user.getId());
            serviceRequest.setProductId(productId);
            serviceRequest.setRequestType(requestType);
            serviceRequest.setReason(reason);

            requestDAO.create(serviceRequest);

            redirectWithMessage(
                    request,
                    response,
                    orderId,
                    getSuccessMessage(requestType));

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to create service request.",
                    exception);
        }
    }

    /**
     * Checks whether the session belongs to a buyer.
     *
     * @param session HTTP session
     * @return true when the session belongs to a buyer
     */
    private boolean isBuyer(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User
                && User.ROLE_BUYER.equals(
                        ((User) userObject).getRole());
    }

    /**
     * Validates the service request type.
     *
     * @param requestType request type
     * @return true when the type is supported
     */
    private boolean isValidType(
            final String requestType) {

        return TYPE_RETURN.equals(requestType)
                || TYPE_REPLACEMENT.equals(requestType)
                || TYPE_NEED_HELP.equals(requestType);
    }

    /**
     * Creates a success message.
     *
     * @param requestType request type
     * @return success message
     */
    private String getSuccessMessage(
            final String requestType) {

        if (TYPE_RETURN.equals(requestType)) {
            return "Return request submitted successfully.";
        }

        if (TYPE_REPLACEMENT.equals(requestType)) {
            return "Replacement request submitted successfully.";
        }

        return "Help request submitted successfully.";
    }

    /**
     * Removes surrounding whitespace.
     *
     * @param value input value
     * @return cleaned value
     */
    private String clean(final String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Redirects to order details with a message.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param orderId order ID
     * @param message message
     * @throws IOException when redirect fails
     */
    private void redirectWithMessage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final long orderId,
            final String message) throws IOException {

        final String encodedMessage =
                URLEncoder.encode(
                        message,
                        StandardCharsets.UTF_8);

        response.sendRedirect(
                request.getContextPath()
                        + ORDER_DETAILS_URL
                        + orderId
                        + "&message="
                        + encodedMessage);
    }

    /**
     * Gets the configured application data source.
     *
     * @param request HTTP request
     * @return application data source
     * @throws ServletException when data source is unavailable
     */
    private DataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener
                                        .DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (DataSource) source;
    }
}
