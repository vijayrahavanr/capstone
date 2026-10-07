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
import java.util.List;

/**
 * Handles seller service request management.
 */
@WebServlet("/seller/service-requests")
public final class SellerServiceRequestServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Service request JSP page. */
    private static final String SERVICE_REQUEST_PAGE =
            "/seller/service-requests.jsp";

    /** Seller dashboard URL. */
    private static final String SELLER_DASHBOARD =
            "/seller/dashboard";

    /** Requested status. */
    private static final String STATUS_REQUESTED =
            "REQUESTED";

    /** Approved status. */
    private static final String STATUS_APPROVED =
            "APPROVED";

    /** Rejected status. */
    private static final String STATUS_REJECTED =
            "REJECTED";

    /** Completed status. */
    private static final String STATUS_COMPLETED =
            "COMPLETED";

    /**
     * Displays seller service requests.
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
                    request.getContextPath() + "/login");
            return;
        }

        final User seller =
                (User) session.getAttribute("user");

        try {
            final OrderServiceRequestDAO requestDAO =
                    new OrderServiceRequestDAO(
                            getDataSource(request));

            final List<OrderServiceRequest> requests =
                    requestDAO.findBySeller(
                            seller.getId());

            request.setAttribute(
                    "serviceRequests",
                    requests);

            request.getRequestDispatcher(
                    SERVICE_REQUEST_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load service requests.",
                    exception);
        }
    }

    /**
     * Updates seller service request status.
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

        if (!isSeller(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final User seller =
                (User) session.getAttribute("user");

        final String requestIdValue =
                request.getParameter("requestId");

        final String newStatus =
                request.getParameter("status");

        final long requestId;

        try {
            requestId =
                    Long.parseLong(requestIdValue);
        } catch (NumberFormatException exception) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/seller/service-requests");
            return;
        }

        if (!isValidStatus(newStatus)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/seller/service-requests");
            return;
        }

        try {
            final OrderServiceRequestDAO requestDAO =
                    new OrderServiceRequestDAO(
                            getDataSource(request));

            requestDAO.updateSellerStatus(
                    requestId,
                    seller.getId(),
                    newStatus);

            response.sendRedirect(
                    request.getContextPath()
                            + "/seller/service-requests");

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to update service request.",
                    exception);
        }
    }

    /**
     * Checks whether session belongs to seller.
     *
     * @param session HTTP session
     * @return true when seller session exists
     */
    private boolean isSeller(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User
                && User.ROLE_SELLER.equals(
                        ((User) userObject).getRole());
    }

    /**
     * Validates seller request status.
     *
     * @param status requested status
     * @return true when supported
     */
    private boolean isValidStatus(
            final String status) {

        return STATUS_APPROVED.equals(status)
                || STATUS_REJECTED.equals(status)
                || STATUS_COMPLETED.equals(status);
    }

    /**
     * Gets application data source.
     *
     * @param request HTTP request
     * @return data source
     * @throws ServletException when unavailable
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
