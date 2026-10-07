package com.vrmart.util;



import javax.sql.DataSource;

import java.math.BigDecimal;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;



/**

 * Seeds demo marketplace products for VR Mart.

 */

public final class ProductSeeder {



    /** Number of products inserted by this seeder. */

    private static final int PRODUCT_COUNT = 45;



    /** First prepared statement parameter. */

    private static final int PARAM_ONE = 1;



    /** Second prepared statement parameter. */

    private static final int PARAM_TWO = 2;



    /** Third prepared statement parameter. */

    private static final int PARAM_THREE = 3;



    /** Fourth prepared statement parameter. */

    private static final int PARAM_FOUR = 4;



    /** Fifth prepared statement parameter. */

    private static final int PARAM_FIVE = 5;



    /** Sixth prepared statement parameter. */

    private static final int PARAM_SIX = 6;



    /** Seventh prepared statement parameter. */

    private static final int PARAM_SEVEN = 7;



    /** Eighth prepared statement parameter. */

    private static final int PARAM_EIGHT = 8;



    /** Ninth prepared statement parameter. */

    private static final int PARAM_NINE = 9;

    /** Product stock value index. */
    private static final int VALUE_THREE = 3;

    /** Product category value index. */
    private static final int VALUE_FOUR = 4;

    /** Product image URL value index. */
    private static final int VALUE_FIVE = 5;



    /** Seller lookup SQL. */

    private static final String SELLER_SQL =

            "SELECT id FROM users "

                    + "WHERE role = 'SELLER' "

                    + "ORDER BY id LIMIT 1";



    /** Product insertion SQL. */

    private static final String INSERT_SQL =

            "INSERT INTO products "

                    + "(seller_id, name, description, price, stock_qty, "

                    + "category, image_url, is_active) "

                    + "SELECT ?, ?, ?, ?, ?, ?, ?, TRUE "

                    + "WHERE NOT EXISTS ("

                    + "SELECT 1 FROM products "

                    + "WHERE seller_id = ? AND name = ?)";



    /**

     * Product seed data.

     *

     * Each row contains:

     * name, description, price, stock, category, image URL.

     */

    private static final String[][] PRODUCTS = {



        {

            "Wireless Bluetooth Headphones",

            "Over ear wireless headphones with clear sound and deep bass.",

            "1499",

            "25",

            "Electronics",

            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Smart Watch AMOLED",

            "Modern AMOLED smartwatch with fitness and notification features.",

            "2499",

            "20",

            "Electronics",

            "https://images.unsplash.com/photo-1523275335684-37898b6baf30"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Wireless Gaming Mouse",

            "Ergonomic wireless mouse designed for gaming and productivity.",

            "1299",

            "30",

            "Electronics",

            "https://images.unsplash.com/photo-1527814050087-3793815479db"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Mechanical Gaming Keyboard",

            "RGB mechanical keyboard with responsive gaming switches.",

            "2899",

            "18",

            "Electronics",

            "https://images.unsplash.com/photo-1587829741301-dc798b83add3"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Portable Bluetooth Speaker",

            "Compact portable speaker with powerful wireless audio.",

            "1799",

            "22",

            "Electronics",

            "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Men's Casual Cotton Shirt",

            "Comfortable regular fit cotton casual shirt.",

            "899",

            "35",

            "Fashion",

            "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Women's Elegant Kurti",

            "Comfortable printed kurti suitable for casual occasions.",

            "1099",

            "28",

            "Fashion",

            "https://images.unsplash.com/photo-1610030469983-98e550d6193c"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Classic Denim Jacket",

            "Classic blue denim jacket for everyday styling.",

            "1599",

            "20",

            "Fashion",

            "https://images.unsplash.com/photo-1551537482-f2075a1d41f2"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Premium Cotton T-Shirt",

            "Soft premium cotton t-shirt with a modern fit.",

            "699",

            "45",

            "Fashion",

            "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Casual White Sneakers",

            "Minimal white sneakers for everyday casual wear.",

            "1899",

            "24",

            "Fashion",

            "https://images.unsplash.com/photo-1542291026-7eec264c27ff"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Electric Kettle",

            "Fast boiling electric kettle with automatic shutoff.",

            "999",

            "25",

            "Home & Kitchen",

            "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Non Stick Cookware Set",

            "Durable non stick cookware set for everyday cooking.",

            "2499",

            "15",

            "Home & Kitchen",

            "https://images.unsplash.com/photo-1556911220-e15b29be8c8f"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Kitchen Storage Containers",

            "Airtight food storage container set for organized kitchens.",

            "799",

            "40",

            "Home & Kitchen",

            "https://images.unsplash.com/photo-1584622650111-993a426fbf0a"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Modern Table Lamp",

            "Elegant table lamp for bedrooms and study spaces.",

            "1199",

            "21",

            "Home & Kitchen",

            "https://images.unsplash.com/photo-1507473885765-e6ed057f782c"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Premium Bedsheet Set",

            "Soft and comfortable bedsheet set with matching pillow covers.",

            "1399",

            "26",

            "Home & Kitchen",

            "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Face Moisturizer",

            "Lightweight daily moisturizer suitable for normal skin.",

            "549",

            "30",

            "Beauty",

            "https://images.unsplash.com/photo-1556228578-8c89e6adf883"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Vitamin C Face Serum",

            "Daily facial serum designed for a fresh skin appearance.",

            "799",

            "25",

            "Beauty",

            "https://images.unsplash.com/photo-1620916566398-39f1143ab7be"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Hair Care Shampoo",

            "Gentle everyday shampoo for clean and healthy looking hair.",

            "399",

            "40",

            "Beauty",

            "https://images.unsplash.com/photo-1556229010-6c3f2c9ca5f8"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Perfume Gift Set",

            "Elegant fragrance gift set for everyday occasions.",

            "1299",

            "18",

            "Beauty",

            "https://images.unsplash.com/photo-1541643600914-78b084683601"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Makeup Brush Set",

            "Complete soft bristle makeup brush set.",

            "699",

            "32",

            "Beauty",

            "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Organic Basmati Rice",

            "Premium long grain basmati rice for everyday meals.",

            "699",

            "50",

            "Grocery",

            "https://images.unsplash.com/photo-1586201375761-83865001e31c"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Premium Coffee Beans",

            "Fresh roasted coffee beans with a rich aroma.",

            "549",

            "35",

            "Grocery",

            "https://images.unsplash.com/photo-1447933601403-0c6688de566e"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Mixed Dry Fruits",

            "Healthy mixed dry fruits packed for freshness.",

            "899",

            "27",

            "Grocery",

            "https://images.unsplash.com/photo-1599599810769-bcde5a160d32"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Green Tea Pack",

            "Refreshing green tea leaves for daily consumption.",

            "299",

            "45",

            "Grocery",

            "https://images.unsplash.com/photo-1556881286-fc6915169721"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Premium Honey",

            "Natural honey suitable for drinks and breakfast.",

            "449",

            "38",

            "Grocery",

            "https://images.unsplash.com/photo-1587049352846-4a222e784d38"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Football",

            "Durable training football suitable for outdoor play.",

            "799",

            "20",

            "Sports & Fitness",

            "https://images.unsplash.com/photo-1553778263-73a83bab9b0c"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Cricket Bat",

            "Lightweight cricket bat designed for practice sessions.",

            "1499",

            "15",

            "Sports & Fitness",

            "https://images.unsplash.com/photo-1531415074968-036ba1b575da"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Yoga Mat",

            "Non slip fitness yoga mat with comfortable cushioning.",

            "699",

            "35",

            "Sports & Fitness",

            "https://images.unsplash.com/photo-1592432678016-e910b452f9a2"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Adjustable Dumbbells",

            "Compact adjustable dumbbell set for home workouts.",

            "2299",

            "16",

            "Sports & Fitness",

            "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Running Sports Shoes",

            "Comfortable lightweight shoes for running and training.",

            "1999",

            "22",

            "Sports & Fitness",

            "https://images.unsplash.com/photo-1552674605-db6ffd4facb5"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Java Programming Book",

            "Beginner friendly guide to Java programming concepts.",

            "599",

            "25",

            "Books",

            "https://images.unsplash.com/photo-1532012197267-da84d127e765"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Data Structures Handbook",

            "Practical introduction to data structures and algorithms.",

            "699",

            "20",

            "Books",

            "https://images.unsplash.com/photo-1544947950-fa07a98d237f"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Python Programming Guide",

            "Complete beginner guide to Python programming.",

            "649",

            "28",

            "Books",

            "https://images.unsplash.com/photo-1515879218367-8466d910aaa4"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Web Development Essentials",

            "Learn modern HTML CSS and JavaScript fundamentals.",

            "749",

            "18",

            "Books",

            "https://images.unsplash.com/photo-1495446815901-a7297e633e8d"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Artificial Intelligence Basics",

            "Introduction to machine learning and artificial intelligence.",

            "899",

            "15",

            "Books",

            "https://images.unsplash.com/photo-1481627834876-b7833e8f5570"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Building Blocks Set",

            "Creative building blocks set for kids.",

            "899",

            "30",

            "Toys",

            "https://images.unsplash.com/photo-1587654780291-39c9404d746b"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Remote Control Car",

            "Fun remote control car with rechargeable battery.",

            "1299",

            "20",

            "Toys",

            "https://images.unsplash.com/photo-1594787318286-3d835c1d207f"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Educational Puzzle",

            "Colorful educational puzzle for children.",

            "399",

            "35",

            "Toys",

            "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Plush Teddy Bear",

            "Soft plush teddy bear suitable as a gift.",

            "599",

            "24",

            "Toys",

            "https://images.unsplash.com/photo-1559454403-b8fb88521f11"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Kids Drawing Kit",

            "Creative drawing and coloring kit for children.",

            "499",

            "32",

            "Toys",

            "https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0"

                    + "?auto=format&fit=crop&w=800&q=80"

        },



        {

            "Leather Wallet",

            "Compact everyday wallet with multiple card slots.",

            "799",

            "25",

            "Accessories",

            "https://images.unsplash.com/photo-1627123424574-724758594e93"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Classic Sunglasses",

            "Stylish sunglasses with a classic everyday frame.",

            "999",

            "22",

            "Accessories",

            "https://images.unsplash.com/photo-1511499767150-a48a237f0083"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Travel Backpack",

            "Spacious backpack for college, office and travel.",

            "1299",

            "30",

            "Accessories",

            "https://images.unsplash.com/photo-1553062407-98eeb64c6a62"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Minimal Wrist Watch",

            "Classic minimal wrist watch with a clean design.",

            "1599",

            "18",

            "Accessories",

            "https://images.unsplash.com/photo-1524805444758-089113d48a6d"

                    + "?auto=format&fit=crop&w=800&q=80"

        },

        {

            "Canvas Laptop Bag",

            "Protective laptop bag suitable for college and office.",

            "1099",

            "26",

            "Accessories",

            "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d"

                    + "?auto=format&fit=crop&w=800&q=80"

        }

    };



    /**

     * Utility class constructor.

     */

    private ProductSeeder() {

        // Utility class.

    }



    /**

     * Seeds marketplace products when a seller exists.

     *

     * @param dataSource application data source

     * @throws SQLException when database access fails

     */

    public static void seed(

            final DataSource dataSource) throws SQLException {



        if (dataSource == null) {

            throw new IllegalArgumentException(

                    "Data source cannot be null.");

        }



        try (Connection connection =

                     dataSource.getConnection()) {



            final Long sellerId =

                    findSellerId(connection);



            if (sellerId == null) {

                return;

            }



            int inserted = 0;



            for (String[] product : PRODUCTS) {



                if (insertProduct(

                        connection,

                        sellerId,

                        product)) {

                    inserted++;

                }

            }



            if (inserted > 0) {

                System.out.println(

                        "VR Mart: seeded "

                                + inserted

                                + " marketplace products.");

            }

        }

    }



    /**

     * Finds the first seller account.

     *

     * @param connection database connection

     * @return seller ID or null

     * @throws SQLException when lookup fails

     */

    private static Long findSellerId(

            final Connection connection)

            throws SQLException {



        try (PreparedStatement statement =

                     connection.prepareStatement(SELLER_SQL);

                ResultSet resultSet =

                        statement.executeQuery()) {



            if (resultSet.next()) {

                return resultSet.getLong(PARAM_ONE);

            }

        }



        return null;

    }



    /**

     * Inserts one product when it does not already exist.

     *

     * @param connection database connection

     * @param sellerId seller identifier

     * @param product product seed data

     * @return true when a product was inserted

     * @throws SQLException when insertion fails

     */

    private static boolean insertProduct(

            final Connection connection,

            final long sellerId,

            final String[] product)

            throws SQLException {



        try (PreparedStatement statement =

                     connection.prepareStatement(INSERT_SQL)) {



            statement.setLong(

                    PARAM_ONE,

                    sellerId);



            statement.setString(

                    PARAM_TWO,

                    product[0]);



            statement.setString(

                    PARAM_THREE,

                    product[1]);



            statement.setBigDecimal(

                    PARAM_FOUR,

                    new BigDecimal(product[2]));



            statement.setInt(

                    PARAM_FIVE,

                    Integer.parseInt(product[VALUE_THREE]));



            statement.setString(

                    PARAM_SIX,

                    product[VALUE_FOUR]);



            statement.setString(

                    PARAM_SEVEN,

                    product[VALUE_FIVE]);



            statement.setLong(

                    PARAM_EIGHT,

                    sellerId);



            statement.setString(

                    PARAM_NINE,

                    product[0]);



            return statement.executeUpdate() > 0;

        }

    }



    /**

     * Returns the number of products supplied by the seeder.

     *

     * @return product count

     */

    public static int getProductCount() {

        return PRODUCT_COUNT;

    }

}
