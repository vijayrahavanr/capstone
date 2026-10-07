package com.vrmart.filter;

import com.vrmart.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Protects role-specific VR Mart routes.
 */
@WebFilter(urlPatterns = {
    "/buyer/*",
    "/seller/*",
    "/admin/*"
})
public final class SecurityFilter implements Filter {

    /** Buyer route prefix. */
    private static final String BUYER_PREFIX = "/buyer/";

    /** Seller route prefix. */
    private static final String SELLER_PREFIX = "/seller/";

    /** Admin route prefix. */
    private static final String ADMIN_PREFIX = "/admin/";

    /** Buyer login route. */
    private static final String BUYER_LOGIN = "/buyer/login";

    /** Buyer registration route. */
    private static final String BUYER_REGISTER = "/buyer/register";

    /** Seller login route. */
    private static final String SELLER_LOGIN = "/seller/login";

    /** Seller registration route. */
    private static final String SELLER_REGISTER = "/seller/register";

    /** Admin login route. */
    private static final String ADMIN_LOGIN = "/admin/login";

    /** Login route. */
    private static final String LOGIN = "/login";

    /** Buyer login page. */
    private static final String BUYER_LOGIN_PAGE = "/buyer/login.jsp";

    /** Seller login page. */
    private static final String SELLER_LOGIN_PAGE = "/seller/login.jsp";

    /** Admin login page. */
    private static final String ADMIN_LOGIN_PAGE = "/admin/login.jsp";

    /**
     * Filters protected VR Mart requests.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param chain filter chain
     * @throws IOException when request processing fails
     * @throws ServletException when filter processing fails
     */
    @Override
    public void doFilter(
            final javax.servlet.ServletRequest request,
            final javax.servlet.ServletResponse response,
            final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest =
                (HttpServletRequest) request;
        final HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        addSecurityHeaders(httpResponse);

        final String path = getPath(httpRequest);

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        final HttpSession session = httpRequest.getSession(false);
        final Object userObject =
                session == null ? null : session.getAttribute("user");
        final Object roleObject =
                session == null ? null : session.getAttribute("role");

        if (!(userObject instanceof User)
                || !(roleObject instanceof String)
                || !isAllowedRole(path, (String) roleObject)) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + getLoginPath(path));
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Adds browser security headers.
     *
     * @param response HTTP response
     */
    private void addSecurityHeaders(
            final HttpServletResponse response) {
        response.setHeader(
                "X-Content-Type-Options",
                "nosniff");
        response.setHeader(
                "X-Frame-Options",
                "SAMEORIGIN");
        response.setHeader(
                "Referrer-Policy",
                "strict-origin-when-cross-origin");
        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
    }

    /**
     * Returns the application-relative request path.
     *
     * @param request HTTP request
     * @return request path
     */
    private String getPath(final HttpServletRequest request) {
        final String contextPath = request.getContextPath();
        final String uri = request.getRequestURI();
        return uri.substring(contextPath.length());
    }

    /**
     * Checks paths that must remain publicly accessible.
     *
     * @param path request path
     * @return true when public
     */
    private boolean isPublicPath(final String path) {
        return BUYER_LOGIN.equals(path)
                || BUYER_REGISTER.equals(path)
                || SELLER_LOGIN.equals(path)
                || SELLER_REGISTER.equals(path)
                || ADMIN_LOGIN.equals(path)
                || BUYER_LOGIN_PAGE.equals(path)
                || SELLER_LOGIN_PAGE.equals(path)
                || ADMIN_LOGIN_PAGE.equals(path)
                || LOGIN.equals(path);
    }

    /**
     * Checks whether the session role matches the requested area.
     *
     * @param path request path
     * @param role session role
     * @return true when access is allowed
     */
    private boolean isAllowedRole(
            final String path,
            final String role) {
        if (path.startsWith(BUYER_PREFIX)) {
            return User.ROLE_BUYER.equals(role);
        }
        if (path.startsWith(SELLER_PREFIX)) {
            return User.ROLE_SELLER.equals(role);
        }
        if (path.startsWith(ADMIN_PREFIX)) {
            return User.ROLE_ADMIN.equals(role);
        }
        return false;
    }

    /**
     * Returns the correct login route for a protected area.
     *
     * @param path request path
     * @return login route
     */
    private String getLoginPath(final String path) {
        if (path.startsWith(BUYER_PREFIX)) {
            return BUYER_LOGIN;
        }
        if (path.startsWith(SELLER_PREFIX)) {
            return SELLER_LOGIN;
        }
        return ADMIN_LOGIN;
    }
}
