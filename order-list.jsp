<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Registry — BookStore Terminal</title>
    
    <!-- Google Fonts: Plus Jakarta Sans & JetBrains Mono -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600;700&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    
    <!-- Bootstrap 5 CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

    <style>
        :root {
            --primary-accent: #4f46e5;
            --primary-hover: #4338ca;
            --surface-bg: #f8fafc;
            --card-border: #e2e8f0;
            --text-heading: #0f172a;
            --text-body: #475569;
        }

        body {
            font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
            background-color: var(--surface-bg);
            background-image: 
                radial-gradient(at 0% 0%, rgba(99, 102, 241, 0.08) 0px, transparent 50%),
                radial-gradient(at 100% 100%, rgba(16, 185, 129, 0.05) 0px, transparent 50%);
            background-attachment: fixed;
            color: var(--text-body);
            min-height: 100vh;
        }

        .font-mono {
            font-family: 'JetBrains Mono', monospace !important;
        }

        /* Floating Island Navbar */
        .floating-nav {
            max-width: 960px;
            background: rgba(255, 255, 255, 0.85);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(226, 232, 240, 0.9);
            box-shadow: 0 10px 25px -5px rgba(15, 23, 42, 0.05), 0 8px 10px -6px rgba(15, 23, 42, 0.03);
            border-radius: 9999px;
            transition: all 0.3s ease;
        }

        .nav-pill-link {
            font-size: 0.875rem;
            font-weight: 600;
            color: #64748b;
            padding: 0.5rem 1.1rem;
            border-radius: 9999px;
            text-decoration: none;
            transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .nav-pill-link:hover {
            color: var(--primary-accent);
            background: rgba(79, 70, 229, 0.08);
        }

        .nav-pill-link.active {
            color: #ffffff;
            background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.25);
        }

        /* Metric Chips */
        .metric-chip {
            background: #ffffff;
            border: 1px solid var(--card-border);
            border-radius: 1rem;
            padding: 1rem 1.25rem;
            box-shadow: 0 4px 6px -1px rgba(15, 23, 42, 0.03);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .metric-chip:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 15px -3px rgba(15, 23, 42, 0.06);
        }

        /* Glass Shell Table Container */
        .glass-shell {
            background: #ffffff;
            border: 1px solid var(--card-border);
            border-radius: 1.5rem;
            box-shadow: 0 20px 35px -10px rgba(15, 23, 42, 0.05), 0 1px 3px rgba(15, 23, 42, 0.04);
            overflow: hidden;
        }

        /* Custom Modern Table */
        .table-custom {
            margin-bottom: 0;
            border-collapse: separate;
            border-spacing: 0;
        }

        .table-custom thead th {
            background-color: #f8fafc;
            color: #64748b;
            font-size: 0.72rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            padding: 1rem 1.25rem;
            border-bottom: 1px solid var(--card-border);
            border-top: none;
        }

        .table-custom tbody tr {
            transition: background-color 0.15s ease;
        }

        .table-custom tbody tr:hover {
            background-color: #f8fafc;
        }

        .table-custom tbody td {
            padding: 1.15rem 1.25rem;
            vertical-align: middle;
            border-bottom: 1px solid var(--card-border);
            color: #334155;
            font-size: 0.875rem;
        }

        .table-custom tbody tr:last-child td {
            border-bottom: none;
        }

        /* User Avatar Bubble */
        .avatar-initial {
            width: 34px;
            height: 34px;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 0.8125rem;
            background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
            color: #3730a3;
        }

        /* Order Type Badges */
        .badge-standard {
            background: rgba(79, 70, 229, 0.1);
            color: #4f46e5;
            border: 1px solid rgba(79, 70, 229, 0.2);
            font-weight: 600;
            font-size: 0.7rem;
            letter-spacing: 0.03em;
        }

        .badge-discounted {
            background: rgba(16, 185, 129, 0.1);
            color: #059669;
            border: 1px solid rgba(16, 185, 129, 0.25);
            font-weight: 600;
            font-size: 0.7rem;
            letter-spacing: 0.03em;
        }

        /* Action Buttons */
        .btn-action-view {
            background: #f0f9ff;
            color: #0284c7;
            border: 1px solid #bae6fd;
            border-radius: 0.5rem;
            padding: 0.4rem 0.7rem;
            font-size: 0.8125rem;
            font-weight: 600;
            transition: all 0.2s ease;
            text-decoration: none;
        }

        .btn-action-view:hover {
            background: #0284c7;
            color: #ffffff;
            box-shadow: 0 4px 10px rgba(2, 132, 199, 0.25);
        }

        .btn-action-edit {
            background: #fffbeb;
            color: #d97706;
            border: 1px solid #fde68a;
            border-radius: 0.5rem;
            padding: 0.4rem 0.7rem;
            font-size: 0.8125rem;
            font-weight: 600;
            transition: all 0.2s ease;
            text-decoration: none;
        }

        .btn-action-edit:hover {
            background: #d97706;
            color: #ffffff;
            box-shadow: 0 4px 10px rgba(217, 119, 6, 0.25);
        }

        .btn-action-delete {
            background: #fef2f2;
            color: #dc2626;
            border: 1px solid #fecaca;
            border-radius: 0.5rem;
            padding: 0.4rem 0.7rem;
            font-size: 0.8125rem;
            font-weight: 600;
            transition: all 0.2s ease;
            text-decoration: none;
        }

        .btn-action-delete:hover {
            background: #dc2626;
            color: #ffffff;
            box-shadow: 0 4px 10px rgba(220, 38, 38, 0.25);
        }

        .btn-pill-primary {
            background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
            color: #ffffff;
            border: none;
            border-radius: 9999px;
            padding: 0.65rem 1.6rem;
            font-weight: 700;
            font-size: 0.875rem;
            letter-spacing: 0.02em;
            box-shadow: 0 8px 18px -3px rgba(79, 70, 229, 0.35);
            transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }

        .btn-pill-primary:hover {
            color: #ffffff;
            transform: translateY(-2px);
            box-shadow: 0 12px 24px -4px rgba(79, 70, 229, 0.45);
        }
    </style>
</head>
<body>

    <!-- Floating Island Navbar -->
    <header class="container pt-3">
        <nav class="navbar floating-nav px-4 py-2 mx-auto">
            <a class="navbar-brand d-flex align-items-center gap-2 fw-bold text-dark mb-0" href="${pageContext.request.contextPath}/orders">
                <span class="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle" style="width: 32px; height: 32px;">
                    <i class="bi bi-book-half" style="font-size: 0.95rem;"></i>
                </span>
                <span>PageCraft <span class="badge bg-indigo-subtle text-primary fw-medium small ms-1" style="font-size: 0.7rem; background-color: #e0e7ff;">Store</span></span>
            </a>
            <div class="d-flex align-items-center gap-2">
                <a class="nav-pill-link active" href="${pageContext.request.contextPath}/orders">
                    <i class="bi bi-receipt me-1"></i>Orders
                </a>
                <a class="nav-pill-link" href="${pageContext.request.contextPath}/orders/new">
                    <i class="bi bi-plus-circle me-1"></i>New Order
                </a>
            </div>
        </nav>
    </header>

    <main class="container py-4">
        <!-- Hero Header & Metric Stats -->
        <div class="row align-items-end justify-content-between mb-4 g-3">
            <div class="col-md-6">
                <span class="badge rounded-pill text-uppercase px-3 py-1 mb-2 font-mono" style="font-size: 0.7rem; background: #e0e7ff; color: #4338ca;">
                    Ledger Database
                </span>
                <h2 class="fw-bold text-dark mb-1">Customer Order Registry</h2>
                <p class="text-muted small mb-0">
                    Real-time transactional log backed by atomic file persistence (<code>data/orders.txt</code>).
                </p>
            </div>
            <div class="col-md-6 text-md-end">
                <a href="${pageContext.request.contextPath}/orders/new" class="btn btn-pill-primary">
                    <i class="bi bi-plus-lg me-2"></i>Place New Order
                </a>
            </div>
        </div>

        <!-- Orders Table in Glass Shell -->
        <div class="glass-shell">
            <div class="table-responsive">
                <table class="table table-custom align-middle">
                    <thead>
                        <tr>
                            <th scope="col" class="ps-4">Order ID &amp; Type</th>
                            <th scope="col">Customer</th>
                            <th scope="col">Book Title &amp; Total</th>
                            <th scope="col" class="text-center">Quantity</th>
                            <th scope="col">Payment Method</th>
                            <th scope="col">Order Date</th>
                            <th scope="col" class="text-center pe-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty orders}">
                                <c:forEach var="order" items="${orders}">
                                    <tr>
                                        <!-- Order ID & Type Badge -->
                                        <td class="ps-4 text-nowrap">
                                            <div class="fw-bold font-mono text-dark">${order.orderId}</div>
                                            <c:choose>
                                                <c:when test="${order.orderType == 'DISCOUNTED'}">
                                                    <span class="badge rounded-pill badge-discounted mt-1">
                                                        <i class="bi bi-tag-fill me-1"></i>DISCOUNTED
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge rounded-pill badge-standard mt-1">
                                                        <i class="bi bi-shield-check me-1"></i>STANDARD
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <!-- Customer Name with Avatar -->
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="avatar-initial">
                                                    ${order.customerName.length() > 0 ? order.customerName.substring(0, 1).toUpperCase() : 'U'}
                                                </div>
                                                <div>
                                                    <div class="fw-bold text-dark">${order.customerName}</div>
                                                    <small class="text-muted">Verified Customer</small>
                                                </div>
                                            </div>
                                        </td>

                                        <!-- Book Title, Unit Price, and Net Total -->
                                        <td>
                                            <div class="fw-semibold text-dark">${order.bookName}</div>
                                            <small class="text-muted d-block">
                                                @ <fmt:formatNumber value="${order.bookPrice}" type="currency" currencySymbol="$" minFractionDigits="2"/> each
                                            </small>
                                            <div class="small fw-bold text-success font-mono mt-1">
                                                Net Due: <fmt:formatNumber value="${order.calculateFinalTotal()}" type="currency" currencySymbol="$" minFractionDigits="2"/>
                                            </div>
                                        </td>

                                        <!-- Quantity -->
                                        <td class="text-center">
                                            <span class="badge bg-light text-dark border font-mono px-2 py-1 fs-6">
                                                ${order.quantity}
                                            </span>
                                        </td>

                                        <!-- Payment Method -->
                                        <td>
                                            <span class="badge rounded-pill px-3 py-1 font-mono" style="background: #f1f5f9; color: #334155; border: 1px solid #e2e8f0;">
                                                <i class="bi bi-credit-card-2-front me-1 text-primary"></i>${order.paymentMethod}
                                            </span>
                                        </td>

                                        <!-- Order Date -->
                                        <td class="text-muted font-mono text-nowrap">
                                            <i class="bi bi-calendar3 me-1 text-secondary"></i>${order.orderDate}
                                        </td>

                                        <!-- Row Actions -->
                                        <td class="text-center pe-4 text-nowrap">
                                            <div class="d-inline-flex gap-1">
                                                <!-- View Details -->
                                                <a href="${pageContext.request.contextPath}/orders/details/${order.orderId}" 
                                                   class="btn-action-view" title="Financial Invoice">
                                                    <i class="bi bi-eye"></i> Details
                                                </a>
                                                <!-- Edit -->
                                                <a href="${pageContext.request.contextPath}/orders/edit/${order.orderId}" 
                                                   class="btn-action-edit" title="Edit Record">
                                                    <i class="bi bi-pencil"></i> Edit
                                                </a>
                                                <!-- Delete -->
                                                <a href="${pageContext.request.contextPath}/orders/delete/${order.orderId}" 
                                                   class="btn-action-delete" 
                                                   onclick="return confirm('Confirm deletion of order record: ${order.orderId}?');" 
                                                   title="Delete Record">
                                                    <i class="bi bi-trash3"></i> Delete
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center py-5">
                                        <div class="py-4">
                                            <div class="d-inline-flex align-items-center justify-content-center bg-light rounded-circle text-muted mb-3" style="width: 72px; height: 72px;">
                                                <i class="bi bi-inbox fs-2"></i>
                                            </div>
                                            <h5 class="fw-bold text-dark">No Order Records Found</h5>
                                            <p class="text-muted small mb-3">Begin by placing your first customer transaction record.</p>
                                            <a href="${pageContext.request.contextPath}/orders/new" class="btn btn-pill-primary">
                                                <i class="bi bi-plus-lg me-1"></i>Place First Order
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <c:if test="${not empty orders}">
                <div class="px-4 py-3 bg-light border-top d-flex flex-wrap justify-content-between align-items-center small text-muted">
                    <span>Showing <strong>${orders.size()}</strong> stored order transaction(s)</span>
                    <span><i class="bi bi-lock-fill text-secondary me-1"></i>Thread-safe synchronized Notepad I/O</span>
                </div>
            </c:if>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
