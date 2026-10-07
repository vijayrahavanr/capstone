# VR Mart

VR Mart is a Java-based e-commerce web application developed as a capstone project.

The application provides integrated Buyer, Seller, and Admin workflows for product browsing, shopping cart management, checkout, order processing, product management, order tracking, authentication, OTP-based password recovery, reviews, wishlist management, notifications, and service requests.

## Features

### Buyer
- Buyer registration and login
- Email verification
- Secure password validation
- BCrypt password hashing
- Buyer dashboard
- Browse marketplace products
- Product search and suggestions
- Add products to cart
- Increase/decrease cart quantity
- Remove products from cart
- Running cart total
- Wishlist management
- Product reviews and ratings
- Checkout
- Delivery address and landmark collection
- Payment method selection
- Mock payment confirmation flow
- Order creation and history
- Order details and status tracking
- Order cancellation
- Notifications
- Profile and settings
- Change password
- Forgot password and OTP-based password reset
- Resend verification
- Buyer service requests
- AI shopping assistant

### Seller
- Seller registration and login
- Seller dashboard
- Add, view, edit, and soft-delete products
- Product review management
- View incoming orders
- View buyer order information
- View delivery address and landmark
- View payment method
- Update and cancel orders
- Seller service requests
- Seller profile and settings
- Seller reviews

### Admin
- Admin login and dashboard
- User management
- Buyer/Seller role management
- Product management and assurance/control
- Order management and status management
- Service request management
- Reviews and ratings management
- Dashboard statistics
- Sales/revenue analytics
- Administrative actions

## Authentication & Security

- Role-based authentication
- Buyer, Seller, and Admin access separation
- BCrypt password hashing
- Security filter for protected resources
- Email verification
- OTP-based password recovery
- Password reset workflow
- Change password functionality
- Session-based authentication
- Protected role-specific dashboards

## OTP / Email Service

VR Mart supports SMTP-based OTP workflows for password recovery and email verification.

Environment variables:

```text
VRMART_SMTP_HOST
VRMART_SMTP_PORT
VRMART_SMTP_USERNAME
VRMART_SMTP_PASSWORD
VRMART_SMTP_FROM
```

Never commit SMTP passwords, App Passwords, OTPs, API keys, or other secrets to GitHub.

## Order Lifecycle

```text
PENDING
   ↓
CONFIRMED
   ↓
PROCESSING
   ↓
SHIPPED
   ↓
DELIVERED
```

Orders can also support cancellation where permitted by the application workflow.

## Technology Stack

### Backend
- Java 26
- Java Servlets
- JSP
- JDBC
- Maven
- HikariCP
- BCrypt

### Frontend
- HTML5
- CSS3
- JSP
- JavaScript

### Database
- PostgreSQL

### Server
- Apache Tomcat 9

### Testing / Quality
- JUnit
- Checkstyle

### Version Control
- Git
- GitHub

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
```

Layer flow:

```text
JSP / Frontend
      ↓
Controllers / Servlets
      ↓
Service Layer
      ↓
DAO Layer
      ↓
PostgreSQL Database
```

## Main Application Modules

```text
VR Mart
│
├── Buyer
│   ├── Authentication
│   ├── Products
│   ├── Search
│   ├── Cart
│   ├── Wishlist
│   ├── Checkout
│   ├── Orders
│   ├── Reviews
│   ├── Notifications
│   ├── Profile
│   ├── Settings
│   └── Service Requests
│
├── Seller
│   ├── Authentication
│   ├── Dashboard
│   ├── Product Management
│   ├── Incoming Orders
│   ├── Order Management
│   ├── Reviews
│   ├── Profile
│   ├── Settings
│   └── Service Requests
│
└── Admin
    ├── Authentication
    ├── Dashboard
    ├── User Management
    ├── Product Management
    ├── Order Management
    ├── Reviews
    └── Service Requests
```

## Local Setup

### Requirements

- JDK 26
- Apache Tomcat 9
- Maven
- PostgreSQL
- Git

### Clone Repository

```bash
git clone https://github.com/vijayrahavanr/capstone.git
cd capstone
```

### Build

```bash
mvn clean package
```

The generated WAR file is available in:

```text
target/
```

### Deploy to Tomcat

Copy the generated WAR file into the Tomcat `webapps` directory and start Tomcat.

Local URL:

```text
http://localhost:8080/VRMart/
```

## SMTP Configuration

Configure SMTP through environment variables or the server environment:

```text
VRMART_SMTP_HOST=smtp.gmail.com
VRMART_SMTP_PORT=587
VRMART_SMTP_USERNAME=<your-email>
VRMART_SMTP_PASSWORD=<your-app-password>
VRMART_SMTP_FROM=<your-email>
```

Do not store real credentials in source code or GitHub.

## Build Verification

The project has been successfully built with:

```bash
mvn clean package
```

The application has also been successfully deployed to Apache Tomcat 9.

## Functional Testing Status

### Buyer Flow
- Login — PASS
- Product browsing — PASS
- Add to Cart — PASS
- Cart — PASS
- Checkout — PASS
- Order Placement — PASS
- My Orders — PASS

### Seller Flow
- Seller Login — PASS
- Seller Dashboard — PASS
- Add Product — PASS
- Product List — PASS
- Edit Product — PASS
- Delete Product — PASS
- Incoming Orders — PASS
- Order Status Update — PASS

### Admin Flow
- Admin Login — PASS
- Admin Dashboard — PASS
- User Management — PASS
- Product Management — PASS
- Order Management — PASS
- Service Requests — PASS
- Reviews & Ratings — PASS

### Authentication
- Email verification — PASS
- OTP delivery — PASS
- OTP verification — PASS
- Forgot Password — PASS
- Password Reset — PASS
- Login after password reset — PASS

### End-to-End Integration
- Buyer → Seller order flow — PASS
- Buyer → Seller → Admin order consistency — PASS
- Order status update reflected to Buyer — PASS
- Stock display consistency — PASS
- Role-based authentication — PASS
- Admin authentication and dashboard access — PASS

## Cloud Deployment

The application is planned for deployment using Google Cloud.

Target architecture:

```text
GitHub
   ↓
Google Cloud
   ↓
Java + Apache Tomcat
   ↓
PostgreSQL Database
   ↓
SMTP Email Service
   ↓
Public VR Mart URL
```

The objective is to run the complete application in the cloud without depending on the local development machine.

Cloud deployment will include:
- Application deployment
- Database configuration
- Environment variable configuration
- SMTP configuration
- HTTP/HTTPS access
- End-to-end cloud testing

## Repository

https://github.com/vijayrahavanr/capstone

## Project Status

- Functional Development: Complete
- End-to-End Testing: Complete
- GitHub Backup: Complete
- Cloud Deployment: In Progress
- Documentation: Pending

## Security Notice

Never commit the following to the repository:

- Passwords
- Gmail App Passwords
- OTP codes
- Database credentials
- API keys
- Session secrets
- Private keys
- Production environment files containing secrets

Use environment variables or secure cloud configuration for sensitive values.

## Capstone Project

VR Mart demonstrates:

- Web application development
- MVC/layered architecture
- Authentication and authorization
- Database integration
- E-commerce workflows
- Role-based access control
- Email/OTP integration
- Order management
- Cloud deployment
- End-to-end application testing
