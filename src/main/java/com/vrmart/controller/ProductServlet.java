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
import java.util.List;

/**
 * Displays products available in the VR Mart marketplace.
 */
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
            final ProductDAO productDAO =
                    new ProductDAO(dataSource);

            final List<Product> products =
                    productDAO.findAll();

            request.setAttribute("products", products);

            request.getRequestDispatcher(
                    "/buyer/products.jsp")
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load VR Mart products.",
                    exception);
        }
    }
}
