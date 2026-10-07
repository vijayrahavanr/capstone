package com.vrmart.controller;

import com.vrmart.dao.ProductDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.Product;
import com.vrmart.service.AIShoppingService;
import com.vrmart.service.AIShoppingService.AssistantResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Handles VR Mart AI shopping assistant requests.
 */
@WebServlet("/buyer/ai-assistant")
public final class AIShoppingAssistantServlet extends HttpServlet {

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** JSON content type. */
    private static final String JSON_CONTENT_TYPE =
            "application/json;charset=UTF-8";

    /** Query request parameter. */
    private static final String QUERY_PARAMETER = "query";

    /** Database connection attribute. */
    private static final String DATA_SOURCE_ERROR =
            "VR Mart database connection is unavailable.";

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    /**
     * Processes an AI shopping request.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when application processing fails
     * @throws IOException when response writing fails
     */
    private void processRequest(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        final String query = request.getParameter(QUERY_PARAMETER);

        if (query == null || query.trim().isEmpty()) {
            writeError(response, "Please enter a shopping request.");
            return;
        }

        try {
            final DataSource dataSource = getDataSource(request);
            final ProductDAO productDAO = new ProductDAO(dataSource);
            final AIShoppingService service =
                    new AIShoppingService(productDAO);

            final AssistantResponse assistantResponse =
                    service.processQuery(query);

            writeResponse(response, assistantResponse);
        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to process AI shopping request.",
                    exception);
        }
    }

    /**
     * Retrieves the configured database data source.
     *
     * @param request HTTP request
     * @return database data source
     * @throws ServletException when data source is unavailable
     */
    private DataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source = request.getServletContext()
                .getAttribute(DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof DataSource)) {
            throw new ServletException(DATA_SOURCE_ERROR);
        }

        return (DataSource) source;
    }

    /**
     * Writes a successful JSON response.
     *
     * @param response HTTP response
     * @param assistantResponse assistant response
     * @throws IOException when response writing fails
     */
    private void writeResponse(
            final HttpServletResponse response,
            final AssistantResponse assistantResponse)
            throws IOException {

        final StringBuilder json = new StringBuilder();

        json.append("{");
        json.append("\"success\":true,");
        json.append("\"message\":\"")
                .append(escapeJson(assistantResponse.getMessage()))
                .append("\",");
        json.append("\"products\":[");

        final List<Product> products = assistantResponse.getProducts();

        for (int index = 0; index < products.size(); index++) {
            if (index > 0) {
                json.append(",");
            }

            appendProduct(json, products.get(index));
        }

        json.append("]}");

        response.getWriter().write(json.toString());
    }

    /**
     * Appends one product as JSON.
     *
     * @param json JSON builder
     * @param product product
     */
    private void appendProduct(
            final StringBuilder json,
            final Product product) {

        json.append("{");
        json.append("\"id\":").append(product.getId()).append(",");
        json.append("\"name\":\"")
                .append(escapeJson(product.getName()))
                .append("\",");
        json.append("\"description\":\"")
                .append(escapeJson(product.getDescription()))
                .append("\",");
        json.append("\"price\":\"")
                .append(formatPrice(product.getPrice()))
                .append("\",");
        json.append("\"stock\":").append(product.getStockQty()).append(",");
        json.append("\"category\":\"")
                .append(escapeJson(product.getCategory()))
                .append("\",");
        json.append("\"imageUrl\":\"")
                .append(escapeJson(product.getImageUrl()))
                .append("\",");
        json.append("\"seller\":\"")
                .append(escapeJson(product.getSellerName()))
                .append("\",");
        json.append("\"vrMartAssured\":")
                .append(product.isVrMartAssured());
        json.append("}");
    }

    /**
     * Formats a product price.
     *
     * @param price product price
     * @return formatted price
     */
    private String formatPrice(final BigDecimal price) {
        if (price == null) {
            return "0.00";
        }

        return price.setScale(
                2,
                java.math.RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * Writes a JSON error response.
     *
     * @param response HTTP response
     * @param message error message
     * @throws IOException when response writing fails
     */
    private void writeError(
            final HttpServletResponse response,
            final String message)
            throws IOException {

        response.setStatus(
                HttpServletResponse.SC_BAD_REQUEST);

        response.getWriter().write(
                "{\"success\":false,\"message\":\""
                        + escapeJson(message)
                        + "\",\"products\":[]}");
    }

    /**
     * Escapes text for JSON.
     *
     * @param value text value
     * @return escaped text
     */
    private String escapeJson(final String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
