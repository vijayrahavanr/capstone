# VR Mart

VR Mart is a Java-based e-commerce web application developed as a capstone project.

The application provides separate buyer and seller workflows for product browsing, shopping cart management, checkout, order processing, seller product management, and order tracking.

## Features

### Buyer

- Buyer registration and login
- Secure password validation
- BCrypt password hashing
- Buyer dashboard
- Browse marketplace products
- Add products to cart
- Increase/decrease cart quantity
- Remove products from cart
- Running cart total
- Checkout
- Delivery address collection
- Delivery landmark collection
- Payment method selection
- Mock payment flow
- Order creation
- Buyer order history
- Order status tracking

### Seller

- Seller registration and login
- Seller dashboard
- Add products
- View seller products
- Modify products
- Remove products using soft delete
- View incoming orders
- View buyer ID and ordered products
- View delivery address and landmark
- View payment method
- Update order status

### Order Status

Orders support the following lifecycle:

PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED

## Technology Stack

- Java 26
- Maven
- Java Servlets
- JSP
- HTML5
- CSS3
- PostgreSQL
- JDBC
- HikariCP
- BCrypt
- Apache Tomcat 9
- JUnit
- Checkstyle

## Project Architecture

The project follows a layered architecture.

```text
com.vrmart
│
├── controller
├── dao
├── service
├── model
├── dto
├── filter
├── listener
├── util
└── exception