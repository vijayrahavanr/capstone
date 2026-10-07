package com.vrmart.controller;

import com.vrmart.dao.ProductDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides product name suggestions for marketplace search.
 */
@WebServlet("/products/suggestions")
public final class ProductSearchSuggestionServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Maximum number of suggestions. */
    private static final int MAX_SUGGESTIONS = 8;

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(
                "application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        final String search =
                request.getParameter("search");

        if (search == null
                || search.trim().isEmpty()) {

            writeEmptyResponse(response);
            return;
        }

        final String searchTerm =
                search.trim().toLowerCase();

        final Object dataSourceObject =
                getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(dataSourceObject
                instanceof javax.sql.DataSource dataSource)) {

            throw new ServletException(
                    "VR Mart database connection pool is unavailable.");
        }

        try {
            final ProductDAO productDAO =
                    new ProductDAO(dataSource);

            final List<Product> products =
                    productDAO.findAll();

            final PrintWriter writer =
                    response.getWriter();

            final Set<String> addedNames =
                    new HashSet<>();

            writer.print("[");

            int count = 0;

            for (final Product product : products) {

                final String productName =
                        product.getName();

                if (productName == null
                        || productName.trim().isEmpty()) {
                    continue;
                }

                final String cleanName =
                        productName.trim();

                final String normalizedName =
                        cleanName.toLowerCase();

                /*
                 * Suggestions are matched ONLY against
                 * the product name.
                 */
                if (!normalizedName.contains(searchTerm)) {
                    continue;
                }

                /*
                 * Avoid showing the same product name
                 * multiple times when multiple sellers
                 * offer the same product.
                 */
                if (!addedNames.add(normalizedName)) {
                    continue;
                }

                if (count >= MAX_SUGGESTIONS) {
                    break;
                }

                if (count > 0) {
                    writer.print(",");
                }

                writer.print("{");
                writer.print("\"name\":\"");
                writer.print(escapeJson(cleanName));
                writer.print("\"");
                writer.print("}");

                count++;
            }

            writer.print("]");

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load product suggestions.",
                    exception);
        }
    }

    /**
     * Escapes special characters for JSON.
     *
     * @param value value to escape
     * @return JSON-safe value
     */
    private String escapeJson(final String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    /**
     * Writes an empty JSON array.
     *
     * @param response HTTP response
     * @throws IOException when response cannot be written
     */
    private void writeEmptyResponse(
            final HttpServletResponse response)
            throws IOException {

        response.getWriter().print("[]");
    }
}
