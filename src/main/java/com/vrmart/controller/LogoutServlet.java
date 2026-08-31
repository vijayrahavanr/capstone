package com.vrmart.controller;

import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles VR Mart user logout requests.
 */
@WebServlet("/logout")
public final class LogoutServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Buyer login page. */
    private static final String BUYER_LOGIN = "/buyer/login";

    /** Seller login page. */
    private static final String SELLER_LOGIN = "/seller/login";

    /** Common login page for admin or unknown role. */
    private static final String COMMON_LOGIN = "/login";

    /**
     * Handles user logout.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException when redirect fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {

        final HttpSession session = request.getSession(false);

        String role = null;

        if (session != null) {
            final Object roleAttribute = session.getAttribute("role");

            if (roleAttribute != null) {
                role = roleAttribute.toString();
            }

            session.invalidate();
        }

        redirectToLogin(request, response, role);
    }

    /**
     * Handles logout form submissions.
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

        doGet(request, response);
    }

    /**
     * Redirects the user to the correct login page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param role user role
     * @throws IOException when redirect fails
     */
    private void redirectToLogin(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String role) throws IOException {

        final String destination;

        if (User.ROLE_BUYER.equals(role)) {
            destination = BUYER_LOGIN;
        } else if (User.ROLE_SELLER.equals(role)) {
            destination = SELLER_LOGIN;
        } else {
            destination = COMMON_LOGIN;
        }

        response.sendRedirect(
                request.getContextPath() + destination);
    }
}
