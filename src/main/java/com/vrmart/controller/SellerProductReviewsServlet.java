package com.vrmart.controller;

import com.vrmart.dao.ProductReviewDAO;
import com.vrmart.model.ProductReview;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Displays product reviews received by the logged-in seller.
 */
@WebServlet("/seller/reviews")
public final class SellerProductReviewsServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Seller reviews page. */
    private static final String REVIEWS_PAGE = "/seller/reviews.jsp";

    /** Seller login path. */
    private static final String SELLER_LOGIN_PATH = "/seller/login";

    @Override
    protected void doGet(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session = request.getSession(false);

        if (!isSeller(session)) {
            response.sendRedirect(
                    request.getContextPath() + SELLER_LOGIN_PATH);
            return;
        }

        final User user = (User) session.getAttribute("user");

        if (user.getId() == null) {
            session.invalidate();
            response.sendRedirect(
                    request.getContextPath() + SELLER_LOGIN_PATH);
            return;
        }

        try {
            final ProductReviewDAO reviewDAO =
                    new ProductReviewDAO(
                            request.getServletContext());

            final List<ProductReview> reviews =
                    reviewDAO.findBySeller(user.getId());

            request.setAttribute("reviews", reviews);

            request.getRequestDispatcher(REVIEWS_PAGE)
                    .forward(request, response);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load seller reviews.",
                    exception);
        }
    }

    /**
     * Checks whether the session belongs to a seller.
     *
     * @param session current HTTP session
     * @return true when the session belongs to a seller
     */
    private boolean isSeller(final HttpSession session) {
        if (session == null) {
            return false;
        }

        final Object userObject = session.getAttribute("user");

        if (!(userObject instanceof User)) {
            return false;
        }

        final User user = (User) userObject;

        return user.getId() != null
                && User.ROLE_SELLER.equals(user.getRole());
    }
}
