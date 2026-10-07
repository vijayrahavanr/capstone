package com.vrmart.controller;

import com.vrmart.dao.OrderDAO;
import com.vrmart.dao.ProductDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.Product;
import com.vrmart.model.SellerOrderItem;
import com.vrmart.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides seller dashboard analytics and marketplace management statistics.
 */
@WebServlet("/seller/dashboard")
public final class SellerDashboardServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Dashboard JSP page. */
    private static final String DASHBOARD_PAGE =
            "/seller/dashboard.jsp";

    /** Stock quantity considered low. */
    private static final int LOW_STOCK_LIMIT = 5;

    /** Maximum number of best-selling products displayed. */
    private static final int BEST_SELLER_LIMIT = 5;

    /** Percentage multiplier. */
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    /** Handles seller dashboard requests. */
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
            final DataSource dataSource =
                    getDataSource(request);

            final OrderDAO orderDAO =
                    new OrderDAO(dataSource);

            final ProductDAO productDAO =
                    new ProductDAO(dataSource);

            final List<SellerOrderItem> orders =
                    orderDAO.findBySeller(user.getId());

            final List<Product> products =
                    productDAO.findBySeller(user.getId());

            final SellerAnalytics analytics =
                    calculateAnalytics(orders, products);

            setDashboardAttributes(request, analytics);

            request.getRequestDispatcher(
                    DASHBOARD_PAGE)
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load seller dashboard.",
                    exception);
        }
    }

    /**
     * Places calculated analytics into request attributes.
     *
     * @param request current HTTP request
     * @param analytics calculated seller analytics
     */
    private void setDashboardAttributes(
            final HttpServletRequest request,
            final SellerAnalytics analytics) {

        request.setAttribute(
                "sellerTotalProducts",
                analytics.totalProducts);

        request.setAttribute(
                "sellerTotalStock",
                analytics.totalStock);

        request.setAttribute(
                "sellerLowStockCount",
                analytics.lowStockProducts.size());

        request.setAttribute(
                "sellerLowStockProducts",
                analytics.lowStockProducts);

        request.setAttribute(
                "sellerProductsSold",
                analytics.productsSold);

        request.setAttribute(
                "sellerRevenue",
                analytics.revenue);

        request.setAttribute(
                "sellerTotalOrders",
                analytics.totalOrders);

        request.setAttribute(
                "sellerPendingOrders",
                analytics.pendingOrders);

        request.setAttribute(
                "sellerCompletedOrders",
                analytics.completedOrders);

        request.setAttribute(
                "sellerCancelledOrders",
                analytics.cancelledOrders);

        request.setAttribute(
                "sellerBestSellers",
                analytics.bestSellers);

        request.setAttribute(
                "sellerPerformanceRate",
                analytics.performanceRate);

        request.setAttribute(
                "sellerAverageOrderValue",
                analytics.averageOrderValue);
    }

    /**
     * Calculates seller statistics from existing order and product data.
     *
     * @param orders seller order items
     * @param products seller products
     * @return calculated analytics
     */
    private SellerAnalytics calculateAnalytics(
            final List<SellerOrderItem> orders,
            final List<Product> products) {

        final SellerAnalytics analytics =
                new SellerAnalytics();

        calculateInventoryAnalytics(
                products,
                analytics);

        if (orders == null || orders.isEmpty()) {
            return analytics;
        }

        final Set<Long> totalOrderIds =
                new HashSet<>();

        final Set<Long> pendingOrderIds =
                new HashSet<>();

        final Set<Long> completedOrderIds =
                new HashSet<>();

        final Set<Long> cancelledOrderIds =
                new HashSet<>();

        final Map<String, Integer> productSales =
                new HashMap<>();

        processOrders(
                orders,
                analytics,
                totalOrderIds,
                pendingOrderIds,
                completedOrderIds,
                cancelledOrderIds,
                productSales);

        analytics.totalOrders =
                totalOrderIds.size();

        analytics.pendingOrders =
                pendingOrderIds.size();

        analytics.completedOrders =
                completedOrderIds.size();

        analytics.cancelledOrders =
                cancelledOrderIds.size();

        calculatePerformance(analytics);

        analytics.bestSellers =
                buildBestSellers(productSales);

        return analytics;
    }

    /**
     * Calculates inventory-related seller statistics.
     *
     * @param products seller products
     * @param analytics analytics object
     */
    private void calculateInventoryAnalytics(
            final List<Product> products,
            final SellerAnalytics analytics) {

        if (products == null) {
            return;
        }

        analytics.totalProducts =
                products.size();

        for (Product product : products) {

            if (product == null) {
                continue;
            }

            analytics.totalStock +=
                    Math.max(0, product.getStockQty());

            if (product.getStockQty()
                    <= LOW_STOCK_LIMIT) {

                analytics.lowStockProducts.add(product);
            }
        }
    }

    /**
     * Processes seller orders and updates analytics.
     *
     * @param orders seller order items
     * @param analytics analytics object
     * @param totalOrderIds all active order identifiers
     * @param pendingOrderIds pending order identifiers
     * @param completedOrderIds completed order identifiers
     * @param cancelledOrderIds cancelled order identifiers
     * @param productSales product sales map
     */
    private void processOrders(
            final List<SellerOrderItem> orders,
            final SellerAnalytics analytics,
            final Set<Long> totalOrderIds,
            final Set<Long> pendingOrderIds,
            final Set<Long> completedOrderIds,
            final Set<Long> cancelledOrderIds,
            final Map<String, Integer> productSales) {

        for (SellerOrderItem item : orders) {

            if (item == null) {
                continue;
            }

            final long orderId =
                    item.getOrderId();

            final String status =
                    normalizeStatus(item.getStatus());

            if (OrderDAO.STATUS_CANCELLED.equals(status)) {
                cancelledOrderIds.add(orderId);
                continue;
            }

            totalOrderIds.add(orderId);

            if (isPendingStatus(status)) {
                pendingOrderIds.add(orderId);
            }

            if (OrderDAO.STATUS_DELIVERED.equals(status)) {
                processDeliveredItem(
                        item,
                        analytics,
                        productSales);

                completedOrderIds.add(orderId);
            }
        }
    }

    /**
     * Processes a delivered seller order item.
     *
     * @param item delivered order item
     * @param analytics analytics object
     * @param productSales product sales map
     */
    private void processDeliveredItem(
            final SellerOrderItem item,
            final SellerAnalytics analytics,
            final Map<String, Integer> productSales) {

        final int quantity =
                Math.max(0, item.getQuantity());

        analytics.productsSold += quantity;

        final BigDecimal lineTotal =
                item.getLineTotal() != null
                        ? item.getLineTotal()
                        : calculateLineTotal(item);

        if (lineTotal != null) {
            analytics.revenue =
                    analytics.revenue.add(lineTotal);
        }

        final String productName =
                item.getProductName() == null
                        ? "Unknown Product"
                        : item.getProductName();

        productSales.merge(
                productName,
                quantity,
                Integer::sum);
    }

    /**
     * Determines whether an order is currently pending.
     *
     * @param status normalized order status
     * @return true when the status represents an active order
     */
    private boolean isPendingStatus(
            final String status) {

        return OrderDAO.STATUS_PENDING.equals(status)
                || OrderDAO.STATUS_APPROVED.equals(status)
                || OrderDAO.STATUS_SHIPPED.equals(status);
    }

    /**
     * Calculates seller performance metrics.
     *
     * @param analytics analytics object
     */
    private void calculatePerformance(
            final SellerAnalytics analytics) {

        if (analytics.totalOrders > 0) {
            analytics.performanceRate =
                    ((double) analytics.completedOrders
                            / analytics.totalOrders)
                            * PERCENTAGE_MULTIPLIER;
        }

        if (analytics.completedOrders > 0) {
            analytics.averageOrderValue =
                    analytics.revenue.divide(
                            BigDecimal.valueOf(
                                    analytics.completedOrders),
                            2,
                            RoundingMode.HALF_UP);
        }
    }

    /**
     * Builds a list of the highest-selling products.
     *
     * @param productSales product sales quantities
     * @return sorted best-selling products
     */
    private List<Map.Entry<String, Integer>> buildBestSellers(
            final Map<String, Integer> productSales) {

        final List<Map.Entry<String, Integer>> sortedProducts =
                new ArrayList<>(
                        productSales.entrySet());

        sortedProducts.sort(
                Map.Entry
                        .<String, Integer>comparingByValue()
                        .reversed());

        if (sortedProducts.size() <= BEST_SELLER_LIMIT) {
            return sortedProducts;
        }

        return new ArrayList<>(
                sortedProducts.subList(
                        0,
                        BEST_SELLER_LIMIT));
    }

    /**
     * Calculates an order item total when the stored value is unavailable.
     *
     * @param item seller order item
     * @return calculated line total
     */
    private BigDecimal calculateLineTotal(
            final SellerOrderItem item) {

        if (item.getUnitPrice() == null) {
            return BigDecimal.ZERO;
        }

        return item.getUnitPrice()
                .multiply(
                        BigDecimal.valueOf(
                                Math.max(
                                        0,
                                        item.getQuantity())));
    }

    /**
     * Normalizes an order status for reliable comparison.
     *
     * @param status raw order status
     * @return normalized status
     */
    private String normalizeStatus(
            final String status) {

        if (status == null) {
            return "";
        }

        return status.trim().toUpperCase();
    }

    /**
     * Checks whether the current session belongs to a seller.
     *
     * @param session current HTTP session
     * @return true when the session belongs to a seller
     */
    private boolean isSeller(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User user
                && User.ROLE_SELLER.equals(
                        user.getRole())
                && user.getId() != null;
    }

    /**
     * Returns the application database data source.
     *
     * @param request current HTTP request
     * @return configured data source
     * @throws ServletException when data source is unavailable
     */
    private DataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object dataSource =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener
                                        .DATA_SOURCE_ATTRIBUTE);

        if (!(dataSource instanceof DataSource)) {
            throw new ServletException(
                    "VR Mart database connection "
                            + "is unavailable.");
        }

        return (DataSource) dataSource;
    }

    /**
     * Stores calculated seller dashboard analytics.
     */
    private static final class SellerAnalytics {

        /** Number of seller products. */
        private int totalProducts;

        /** Current total inventory quantity. */
        private int totalStock;

        /** Number of products sold. */
        private int productsSold;

        /** Number of active orders. */
        private int totalOrders;

        /** Number of pending orders. */
        private int pendingOrders;

        /** Number of completed orders. */
        private int completedOrders;

        /** Number of cancelled orders. */
        private int cancelledOrders;

        /** Seller revenue from delivered items. */
        private BigDecimal revenue =
                BigDecimal.ZERO;

        /** Average value of completed orders. */
        private BigDecimal averageOrderValue =
                BigDecimal.ZERO;

        /** Percentage of orders completed. */
        private double performanceRate;

        /** Seller products with low stock. */
        private final List<Product> lowStockProducts =
                new ArrayList<>();

        /** Best-selling products. */
        private List<Map.Entry<String, Integer>>
                bestSellers =
                new ArrayList<>();
    }
}
