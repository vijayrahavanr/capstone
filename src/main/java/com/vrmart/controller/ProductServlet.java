package com.vrmart.controller;

import com.vrmart.dao.ProductDAO;
import com.vrmart.dao.ProductReviewDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/products")
public final class ProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final Object dataSourceObject =
                getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(dataSourceObject
                instanceof javax.sql.DataSource dataSource)) {
            throw new ServletException(
                    "VR Mart database connection pool is unavailable.");
        }

        try {
            final ProductDAO productDAO = new ProductDAO(dataSource);
            final ProductReviewDAO reviewDAO =
                    new ProductReviewDAO(request.getServletContext());

            final List<Product> allProducts = productDAO.findAll();

            final String search = request.getParameter("search");
            final String category = request.getParameter("category");
            final String minPrice = request.getParameter("minPrice");
            final String maxPrice = request.getParameter("maxPrice");
            final String availability =
                    request.getParameter("availability");
            final String sort = request.getParameter("sort");

            final String searchTerm =
                    search == null ? "" : search.trim().toLowerCase();
            final String categoryTerm =
                    category == null ? "" : category.trim().toLowerCase();

            final BigDecimal minimumPrice = parsePrice(minPrice);
            final BigDecimal maximumPrice = parsePrice(maxPrice);

            final List<Product> products = filterProducts(
                    allProducts,
                    searchTerm,
                    categoryTerm,
                    minimumPrice,
                    maximumPrice,
                    availability);

            sortProducts(products, sort);

            final Set<String> categories = new HashSet<>();
            for (final Product product : allProducts) {
                if (product.getCategory() != null
                        && !product.getCategory().trim().isEmpty()) {
                    categories.add(product.getCategory().trim());
                }
            }

            final List<String> categoryOptions =
                    new ArrayList<>(categories);
            categoryOptions.sort(String.CASE_INSENSITIVE_ORDER);

            final Map<Long, double[]> productRatings = new HashMap<>();
            for (final Product product : products) {
                productRatings.put(
                        product.getId(),
                        reviewDAO.findProductRating(product.getId()));
            }

            request.setAttribute("products", products);
            request.setAttribute("productRatings", productRatings);
            request.setAttribute("categories", categoryOptions);
            request.setAttribute(
                    "searchTerm",
                    search == null ? "" : search.trim());
            request.setAttribute(
                    "category",
                    category == null ? "" : category.trim());
            request.setAttribute(
                    "minPrice",
                    minPrice == null ? "" : minPrice.trim());
            request.setAttribute(
                    "maxPrice",
                    maxPrice == null ? "" : maxPrice.trim());
            request.setAttribute(
                    "availability",
                    availability == null ? "" : availability.trim());
            request.setAttribute(
                    "sort",
                    sort == null ? "" : sort.trim());

            request.getRequestDispatcher("/buyer/products.jsp")
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load VR Mart products.",
                    exception);
        }
    }

    private List<Product> filterProducts(
            final List<Product> allProducts,
            final String searchTerm,
            final String categoryTerm,
            final BigDecimal minimumPrice,
            final BigDecimal maximumPrice,
            final String availability) {

        final List<Product> filteredProducts = new ArrayList<>();

        for (final Product product : allProducts) {
            final boolean matchesSearch =
                    searchTerm.isEmpty()
                            || containsSearchTerm(
                                    product.getName(), searchTerm)
                            || containsSearchTerm(
                                    product.getCategory(), searchTerm)
                            || containsSearchTerm(
                                    product.getDescription(), searchTerm)
                            || containsSearchTerm(
                                    product.getSellerName(), searchTerm);

            final boolean matchesCategory =
                    categoryTerm.isEmpty()
                            || containsSearchTerm(
                                    product.getCategory(), categoryTerm);

            final boolean matchesMinimumPrice =
                    minimumPrice == null
                            || product.getPrice() != null
                            && product.getPrice().compareTo(
                                    minimumPrice) >= 0;

            final boolean matchesMaximumPrice =
                    maximumPrice == null
                            || product.getPrice() != null
                            && product.getPrice().compareTo(
                                    maximumPrice) <= 0;

            final boolean matchesAvailability =
                    !"in-stock".equalsIgnoreCase(
                            safeString(availability))
                            || product.getStockQty() > 0;

            if (matchesSearch
                    && matchesCategory
                    && matchesMinimumPrice
                    && matchesMaximumPrice
                    && matchesAvailability) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    private void sortProducts(
            final List<Product> products,
            final String sort) {

        final String sortOption = safeString(sort).toLowerCase();

        switch (sortOption) {
            case "price-asc":
                products.sort(
                        Comparator.comparing(
                                (Product product) -> product.getPrice(),
                                Comparator.nullsLast(
                                        BigDecimal::compareTo)));
                break;

            case "price-desc":
                products.sort(
                        Comparator.comparing(
                                (Product product) -> product.getPrice(),
                                Comparator.nullsLast(
                                        BigDecimal::compareTo))
                                .reversed());
                break;

            case "name-asc":
                products.sort(
                        Comparator.comparing(
                                (Product product) ->
                                        safeString(product.getName()),
                                String.CASE_INSENSITIVE_ORDER));
                break;

            case "name-desc":
                products.sort(
                        Comparator.comparing(
                                (Product product) ->
                                        safeString(product.getName()),
                                String.CASE_INSENSITIVE_ORDER)
                                .reversed());
                break;

            case "stock-desc":
                products.sort(
                        Comparator.comparingInt(
                                (Product product) ->
                                        product.getStockQty())
                                .reversed());
                break;

            case "stock-asc":
                products.sort(
                        Comparator.comparingInt(
                                (Product product) ->
                                        product.getStockQty()));
                break;

            default:
                break;
        }
    }

    private BigDecimal parsePrice(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            final BigDecimal price = new BigDecimal(value.trim());

            if (price.compareTo(BigDecimal.ZERO) < 0) {
                return null;
            }

            return price;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private boolean containsSearchTerm(
            final String value,
            final String searchTerm) {

        return value != null
                && value.toLowerCase().contains(searchTerm);
    }

    private String safeString(final String value) {
        return value == null ? "" : value.trim();
    }
}
