package com.vrmart.controller;

import com.vrmart.dao.AdminDAO;
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
 * Handles administrator control-center actions.
 */
@WebServlet("/admin/action")
public final class AdminActionServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Minimum valid database identifier. */
    private static final long MIN_ID = 1L;

    /** User role action. */
    private static final String ACTION_USER_ROLE = "userRole";

    /** Delete user action. */
    private static final String ACTION_DELETE_USER = "deleteUser";

    /** Product active action. */
    private static final String ACTION_PRODUCT_ACTIVE =
            "productActive";

    /** Delete product action. */
    private static final String ACTION_DELETE_PRODUCT =
            "deleteProduct";

    /** Product assurance action. */
    private static final String ACTION_PRODUCT_ASSURANCE =
            "productAssurance";

    /** Order status action. */
    private static final String ACTION_ORDER_STATUS =
            "orderStatus";

    /** True parameter value. */
    private static final String VALUE_TRUE = "true";

    /** False parameter value. */
    private static final String VALUE_FALSE = "false";

    /** Pending order status. */
    private static final String STATUS_PENDING = "PENDING";

    /** Approved order status. */
    private static final String STATUS_APPROVED = "APPROVED";

    /** Shipped order status. */
    private static final String STATUS_SHIPPED = "SHIPPED";

    /** Delivered order status. */
    private static final String STATUS_DELIVERED = "DELIVERED";

    /** Cancelled order status. */
    private static final String STATUS_CANCELLED = "CANCELLED";

    /**
     * Processes administrator actions.
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

        if (!isAdmin(session)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/login");
            return;
        }

        final String action =
                request.getParameter("action");

        try {
            final AdminDAO adminDAO =
                    new AdminDAO(getDataSource(request));

            if (ACTION_USER_ROLE.equals(action)) {
                updateUserRole(
                        request,
                        response,
                        adminDAO,
                        session);
                return;
            }

            if (ACTION_DELETE_USER.equals(action)) {
                deleteUser(
                        request,
                        response,
                        adminDAO,
                        session);
                return;
            }

            if (ACTION_PRODUCT_ACTIVE.equals(action)) {
                updateProductActive(
                        request,
                        response,
                        adminDAO);
                return;
            }

            if (ACTION_DELETE_PRODUCT.equals(action)) {
                deleteProduct(
                        request,
                        response,
                        adminDAO);
                return;
            }

            if (ACTION_PRODUCT_ASSURANCE.equals(action)) {
                updateProductAssurance(
                        request,
                        response,
                        adminDAO,
                        session);
                return;
            }

            if (ACTION_ORDER_STATUS.equals(action)) {
                updateOrderStatus(
                        request,
                        response,
                        adminDAO);
                return;
            }

            redirect(request, response);
        } catch (IllegalArgumentException exception) {
            redirect(request, response);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to process administrator action.",
                    exception);
        }
    }

    /**
     * Updates a user's role.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param adminDAO administrator DAO
     * @param session current session
     * @throws Exception when update fails
     */
    private void updateUserRole(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AdminDAO adminDAO,
            final HttpSession session)
            throws Exception {

        final long userId =
                parseId(
                        request.getParameter("userId"));

        final User currentUser =
                (User) session.getAttribute("user");

        if (userId == currentUser.getId()) {
            redirect(request, response);
            return;
        }

        final String role =
                request.getParameter("role");

        if (!isValidRole(role)) {
            redirect(request, response);
            return;
        }

        adminDAO.updateUserRole(userId, role);
        redirect(request, response);
    }

    /**
     * Deletes a buyer or seller account.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param adminDAO administrator DAO
     * @param session current session
     * @throws Exception when deletion fails
     */
    private void deleteUser(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AdminDAO adminDAO,
            final HttpSession session)
            throws Exception {

        final long userId =
                parseId(
                        request.getParameter("userId"));

        final User admin =
                (User) session.getAttribute("user");

        if (userId == admin.getId()) {
            redirect(request, response);
            return;
        }

        adminDAO.deleteUser(
                userId,
                admin.getId());

        redirect(request, response);
    }

    /**
     * Updates product visibility.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param adminDAO administrator DAO
     * @throws Exception when update fails
     */
    private void updateProductActive(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AdminDAO adminDAO)
            throws Exception {

        final long productId =
                parseId(
                        request.getParameter("productId"));

        final boolean active =
                parseBoolean(
                        request.getParameter("active"));

        adminDAO.updateProductActive(
                productId,
                active);

        redirect(request, response);
    }

    /**
     * Permanently removes an inactive product.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param adminDAO administrator DAO
     * @throws Exception when deletion fails
     */
    private void deleteProduct(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AdminDAO adminDAO)
            throws Exception {

        final long productId =
                parseId(
                        request.getParameter("productId"));

        adminDAO.deleteInactiveProduct(productId);
        redirect(request, response);
    }

    /**
     * Updates VR Mart assurance.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param adminDAO administrator DAO
     * @param session current session
     * @throws Exception when update fails
     */
    private void updateProductAssurance(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AdminDAO adminDAO,
            final HttpSession session)
            throws Exception {

        final long productId =
                parseId(
                        request.getParameter("productId"));

        final boolean assured =
                parseBoolean(
                        request.getParameter("assured"));

        final User admin =
                (User) session.getAttribute("user");

        adminDAO.updateProductAssurance(
                productId,
                admin.getId(),
                assured);

        redirect(request, response);
    }

    /**
     * Updates an order status.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param adminDAO administrator DAO
     * @throws Exception when update fails
     */
    private void updateOrderStatus(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AdminDAO adminDAO)
            throws Exception {

        final long orderId =
                parseId(
                        request.getParameter("orderId"));

        final String status =
                request.getParameter("status");

        if (!isValidOrderStatus(status)) {
            redirect(request, response);
            return;
        }

        adminDAO.updateOrderStatus(
                orderId,
                status);

        redirect(request, response);
    }

    /**
     * Parses a positive database identifier.
     *
     * @param value submitted identifier
     * @return validated identifier
     */
    private long parseId(final String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Identifier is required.");
        }

        final long id =
                Long.parseLong(value.trim());

        if (id < MIN_ID) {
            throw new IllegalArgumentException(
                    "Identifier must be positive.");
        }

        return id;
    }

    /**
     * Parses a strict boolean request value.
     *
     * @param value submitted boolean
     * @return parsed boolean
     */
    private boolean parseBoolean(final String value) {

        if (VALUE_TRUE.equals(value)) {
            return true;
        }

        if (VALUE_FALSE.equals(value)) {
            return false;
        }

        throw new IllegalArgumentException(
                "Invalid boolean value.");
    }

    /**
     * Validates a supported user role.
     *
     * @param role submitted role
     * @return true when valid
     */
    private boolean isValidRole(final String role) {

        return User.ROLE_BUYER.equals(role)
                || User.ROLE_SELLER.equals(role)
                || User.ROLE_ADMIN.equals(role);
    }

    /**
     * Validates a supported order status.
     *
     * @param status submitted order status
     * @return true when valid
     */
    private boolean isValidOrderStatus(
            final String status) {

        return STATUS_PENDING.equals(status)
                || STATUS_APPROVED.equals(status)
                || STATUS_SHIPPED.equals(status)
                || STATUS_DELIVERED.equals(status)
                || STATUS_CANCELLED.equals(status);
    }

    /**
     * Redirects to the control center.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException when redirect fails
     */
    private void redirect(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                        + "/admin/dashboard");
    }

    /**
     * Checks administrator authentication.
     *
     * @param session HTTP session
     * @return true for an administrator
     */
    private boolean isAdmin(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User user
                && User.ROLE_ADMIN.equals(user.getRole())
                && user.getId() != null;
    }

    /**
     * Returns the application data source.
     *
     * @param request HTTP request
     * @return HikariCP data source
     * @throws ServletException when unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener
                                        .DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (HikariDataSource) source;
    }
}
