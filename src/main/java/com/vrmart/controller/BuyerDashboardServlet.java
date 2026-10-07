package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.dao.ProductDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.CartItem;
import com.vrmart.model.Order;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Prepares buyer dashboard data, including personalized recommendations.
 */
@WebServlet("/buyer/dashboard")
public final class BuyerDashboardServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Dashboard page. */
    private static final String DASHBOARD_PAGE = "/buyer/dashboard.jsp";

    /** Maximum number of recommended products. */
    private static final int RECOMMENDATION_LIMIT = 6;

    /** Weight applied to category purchase frequency. */
    private static final int CATEGORY_WEIGHT = 100;

    /** Weight applied to product-name token matches. */
    private static final int NAME_TOKEN_WEIGHT = 20;

    /** Small preference boost for VR Mart assured products. */
    private static final int ASSURED_WEIGHT = 5;

    /** Minimum token length used for product-name matching. */
    private static final int MIN_TOKEN_LENGTH = 3;

    /** Status that must not contribute to buyer preferences. */
    private static final String CANCELLED_STATUS = "CANCELLED";

    /**
     * Loads the buyer dashboard.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when dashboard loading fails
     * @throws IOException when request processing fails
     */
    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session = request.getSession(false);
        final User user = session == null
                ? null
                : (User) session.getAttribute("user");

        if (user == null || !User.ROLE_BUYER.equals(user.getRole())) {
            response.sendRedirect(
                    request.getContextPath() + "/buyer/login");
            return;
        }

        try {
            final DataSource dataSource = getDataSource(request);
            final ProductDAO productDAO = new ProductDAO(dataSource);
            final OrderDAO orderDAO = new OrderDAO(dataSource);
            final List<Product> products = productDAO.findAll();
            final List<Order> orders = orderDAO.findByBuyer(user.getId());
            final RecommendationData recommendationData =
                    buildRecommendations(products, orders, orderDAO,
                            user.getId());

            request.setAttribute(
                    "dashboardLoaded",
                    Boolean.TRUE);
            request.setAttribute(
                    "recommendedProducts",
                    recommendationData.products());
            request.setAttribute(
                    "recommendationMessage",
                    recommendationData.message());
            request.getRequestDispatcher(DASHBOARD_PAGE)
                    .forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute(
                    "dashboardLoaded",
                    Boolean.TRUE);
            request.setAttribute(
                    "recommendedProducts",
                    new ArrayList<Product>());
            request.setAttribute(
                    "recommendationMessage",
                    "Recommendations are temporarily unavailable.");
            request.getRequestDispatcher(DASHBOARD_PAGE)
                    .forward(request, response);
        }
    }

    /**
     * Builds rule-based recommendations from the buyer's order history.
     *
     * @param products active marketplace products
     * @param orders buyer orders
     * @param orderDAO order data access object
     * @param buyerId buyer identifier
     * @return recommendation data
     * @throws SQLException when order items cannot be loaded
     */
    private RecommendationData buildRecommendations(
            final List<Product> products,
            final List<Order> orders,
            final OrderDAO orderDAO,
            final long buyerId) throws SQLException {
        final Map<Long, Product> productById = mapProducts(products);
        final Map<String, Integer> categoryFrequency = new HashMap<>();
        final Set<Long> purchasedProductIds = new HashSet<>();
        final List<String> purchasedNames = new ArrayList<>();

        for (Order order : orders) {
            if (CANCELLED_STATUS.equalsIgnoreCase(order.getStatus())) {
                continue;
            }
            final List<CartItem> items = orderDAO.findItemsByOrder(
                    order.getId(), buyerId);
            for (CartItem item : items) {
                purchasedProductIds.add(item.getProductId());
                final Product purchasedProduct =
                        productById.get(item.getProductId());
                if (purchasedProduct != null) {
                    addCategoryFrequency(
                            categoryFrequency,
                            purchasedProduct.getCategory(),
                            item.getQuantity());
                    purchasedNames.add(purchasedProduct.getName());
                }
            }
        }

        final List<ScoredProduct> scoredProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getStockQty() <= 0
                    || purchasedProductIds.contains(product.getId())) {
                continue;
            }
            final int score = calculateScore(
                    product,
                    categoryFrequency,
                    purchasedNames);
            scoredProducts.add(new ScoredProduct(product, score));
        }

        scoredProducts.sort(
                Comparator.comparingInt(ScoredProduct::score)
                        .reversed()
                        .thenComparing(
                                item -> item.product().getCreatedAt(),
                                Comparator.nullsLast(
                                        Comparator.reverseOrder())));

        final List<Product> recommendations = new ArrayList<>();
        for (int index = 0;
             index < scoredProducts.size()
                     && index < RECOMMENDATION_LIMIT;
             index++) {
            recommendations.add(scoredProducts.get(index).product());
        }

        final String message = categoryFrequency.isEmpty()
                ? "Fresh picks from the VR Mart marketplace."
                : "Based on your shopping history and category preferences.";
        return new RecommendationData(recommendations, message);
    }

    /**
     * Maps active products by product identifier.
     *
     * @param products active products
     * @return product lookup map
     */
    private Map<Long, Product> mapProducts(final List<Product> products) {
        final Map<Long, Product> productById = new HashMap<>();
        for (Product product : products) {
            productById.put(product.getId(), product);
        }
        return productById;
    }

    /**
     * Adds purchased quantity to a category preference.
     *
     * @param categoryFrequency category preference map
     * @param category product category
     * @param quantity purchased quantity
     */
    private void addCategoryFrequency(
            final Map<String, Integer> categoryFrequency,
            final String category,
            final int quantity) {
        if (category == null || category.trim().isEmpty()) {
            return;
        }
        categoryFrequency.merge(category.trim().toLowerCase(),
                quantity,
                Integer::sum);
    }

    /**
     * Calculates the recommendation score for one product.
     *
     * @param product product being scored
     * @param categoryFrequency buyer category preferences
     * @param purchasedNames names of previously purchased products
     * @return recommendation score
     */
    private int calculateScore(
            final Product product,
            final Map<String, Integer> categoryFrequency,
            final List<String> purchasedNames) {
        int score = 0;
        final String category = product.getCategory();
        if (category != null) {
            score += categoryFrequency.getOrDefault(
                    category.trim().toLowerCase(), 0) * CATEGORY_WEIGHT;
        }
        score += matchingNameTokens(product.getName(), purchasedNames)
                * NAME_TOKEN_WEIGHT;
        if (product.isVrMartAssured()) {
            score += ASSURED_WEIGHT;
        }
        return score;
    }

    /**
     * Counts meaningful product-name tokens shared with purchased products.
     *
     * @param productName candidate product name
     * @param purchasedNames previously purchased product names
     * @return number of matching tokens
     */
    private int matchingNameTokens(final String productName,
                                   final List<String> purchasedNames) {
        final Set<String> candidateTokens = tokens(productName);
        int matches = 0;
        for (String purchasedName : purchasedNames) {
            final Set<String> purchasedTokens = tokens(purchasedName);
            for (String token : candidateTokens) {
                if (purchasedTokens.contains(token)) {
                    matches++;
                }
            }
        }
        return matches;
    }

    /**
     * Converts a product name into meaningful lowercase tokens.
     *
     * @param value source text
     * @return normalized tokens
     */
    private Set<String> tokens(final String value) {
        final Set<String> result = new HashSet<>();
        if (value == null) {
            return result;
        }
        final String[] parts = value.toLowerCase().split("[^a-z0-9]+");
        for (String part : parts) {
            if (part.length() >= MIN_TOKEN_LENGTH) {
                result.add(part);
            }
        }
        return result;
    }

    /**
     * Gets the application database connection pool.
     *
     * @param request HTTP request
     * @return database data source
     * @throws ServletException when data source is unavailable
     */
    private DataSource getDataSource(final HttpServletRequest request)
            throws ServletException {
        final Object dataSource = request.getServletContext()
                .getAttribute(DatabaseListener.DATA_SOURCE_ATTRIBUTE);
        if (!(dataSource instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }
        return (DataSource) dataSource;
    }

 /**
 * Stores the generated recommendation result.
 *
 * @param products recommended products
 * @param message recommendation message
 */
private record RecommendationData(
        List<Product> products,
        String message) {
}

/**
 * Stores a product together with its recommendation score.
 *
 * @param product product being scored
 * @param score recommendation score
 */
private record ScoredProduct(Product product, int score) {
}
}
