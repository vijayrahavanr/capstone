<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.vrmart.model.OrderServiceRequest" %>

<%
    final List<OrderServiceRequest> serviceRequests =
            (List<OrderServiceRequest>)
                    request.getAttribute("serviceRequests");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Service Requests</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(
                    135deg,
                    #0b1020,
                    #111827
            );
            color: #ffffff;
        }

        .container {
            width: 92%;
            max-width: 1200px;
            margin: 40px auto;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        h1 {
            margin: 0;
            font-size: 36px;
        }

        .back {
            padding: 12px 20px;
            border-radius: 9px;
            background: #374151;
            color: #ffffff;
            text-decoration: none;
            font-weight: 600;
        }

        .back:hover {
            background: #4b5563;
        }

        .card {
            background: rgba(31, 41, 55, 0.95);
            border-radius: 14px;
            padding: 24px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
        }

        .grid {
            display: grid;
            grid-template-columns:
                repeat(auto-fit, minmax(180px, 1fr));
            gap: 18px;
        }

        .label {
            color: #9ca3af;
            font-size: 13px;
            margin-bottom: 6px;
        }

        .value {
            font-size: 16px;
            font-weight: 600;
        }

        .reason {
            margin-top: 20px;
            padding: 14px;
            border-radius: 8px;
            background: #111827;
            color: #d1d5db;
            line-height: 1.6;
        }

        .status {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            background: #374151;
            font-size: 13px;
        }

        .actions {
            margin-top: 20px;
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        form {
            margin: 0;
        }

        button {
            border: 0;
            padding: 10px 16px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            font-size: 14px;
        }

        .approve {
            background: #16a34a;
            color: #ffffff;
        }

        .approve:hover {
            background: #15803d;
        }

        .reject {
            background: #dc2626;
            color: #ffffff;
        }

        .reject:hover {
            background: #b91c1c;
        }

        .complete {
            background: #2563eb;
            color: #ffffff;
        }

        .complete:hover {
            background: #1d4ed8;
        }

        .empty {
            text-align: center;
            color: #9ca3af;
            padding: 50px 20px;
            font-size: 16px;
        }

        @media (max-width: 600px) {

            .container {
                width: 94%;
                margin: 25px auto;
            }

            .topbar {
                align-items: flex-start;
                gap: 20px;
                flex-direction: column;
            }

            h1 {
                font-size: 30px;
            }

        }

    </style>

</head>

<body>

<div class="container">

    <div class="topbar">

        <h1>Service Requests</h1>

        <a class="back"
           href="<%= request.getContextPath() %>/seller/dashboard.jsp">
            Back to Dashboard
        </a>

    </div>

    <%
        if (serviceRequests == null
                || serviceRequests.isEmpty()) {
    %>

        <div class="card empty">
            No service requests available.
        </div>

    <%
        } else {

            for (OrderServiceRequest serviceRequest
                    : serviceRequests) {
    %>

        <div class="card">

            <div class="grid">

                <div>
                    <div class="label">Order ID</div>
                    <div class="value">
                        #<%= serviceRequest.getOrderId() %>
                    </div>
                </div>

                <div>
                    <div class="label">Buyer</div>
                    <div class="value">
                        <%= serviceRequest.getBuyerName() %>
                    </div>
                </div>

                <div>
                    <div class="label">Product</div>
                    <div class="value">
                        <%= serviceRequest.getProductName() %>
                    </div>
                </div>

                <div>
                    <div class="label">Request</div>
                    <div class="value">
                        <%= serviceRequest.getRequestType() %>
                    </div>
                </div>

                <div>
                    <div class="label">Status</div>
                    <div class="status">
                        <%= serviceRequest.getStatus() %>
                    </div>
                </div>

            </div>

            <div class="reason">

                <div class="label">Reason</div>

                <%= serviceRequest.getReason() %>

            </div>

            <%
                final String status =
                        serviceRequest.getStatus();
            %>

            <div class="actions">

                <% if ("REQUESTED".equals(status)) { %>

                    <form method="post"
                          action="<%= request.getContextPath() %>/seller/service-requests">

                        <input type="hidden"
                               name="requestId"
                               value="<%= serviceRequest.getId() %>">

                        <input type="hidden"
                               name="status"
                               value="APPROVED">

                        <button class="approve"
                                type="submit">
                            Approve
                        </button>

                    </form>

                    <form method="post"
                          action="<%= request.getContextPath() %>/seller/service-requests">

                        <input type="hidden"
                               name="requestId"
                               value="<%= serviceRequest.getId() %>">

                        <input type="hidden"
                               name="status"
                               value="REJECTED">

                        <button class="reject"
                                type="submit">
                            Reject
                        </button>

                    </form>

                <% } else if ("APPROVED".equals(status)) { %>

                    <form method="post"
                          action="<%= request.getContextPath() %>/seller/service-requests">

                        <input type="hidden"
                               name="requestId"
                               value="<%= serviceRequest.getId() %>">

                        <input type="hidden"
                               name="status"
                               value="COMPLETED">

                        <button class="complete"
                                type="submit">
                            Mark Completed
                        </button>

                    </form>

                <% } %>

            </div>

        </div>

    <%
            }
        }
    %>

</div>

</body>

</html>
