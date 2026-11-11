<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Invoice — ${order.orderId} — BookStore Terminal</title>
    
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

        /* Invoice Shell */
        .invoice-card {
            background: #ffffff;
            border: 1px solid var(--card-border);
            border-radius: 1.5rem;
            box-shadow: 0 25px 45px -12px rgba(15, 23, 42, 0.08);
            position: relative;
            overflow: hidden;
        }

        .invoice-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 6px;
            background: linear-gradient(90deg, #4f46e5 0%, #10b981 100%);
        }

        .invoice-meta-box {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 1rem;
            padding: 1.25rem 1.5rem;
        }

        /* Financial Line Item Table */
        .table-invoice {
            margin-bottom: 0;
            border-collapse: separate;
            border-spacing: 0;
        }

        .table-invoice th {
            background-color: #f8fafc;
            color: #64748b;
            font-size: 0.72rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            padding: 0.85rem 1.25rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .table-invoice td {
            padding: 1.1rem 1.25rem;
            border-bottom: 1px solid #e2e8f0;
            color: #334155;
            font-size: 0.9375rem;
        }

        /* Total Banner */
        .grand-total-banner {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            border-radius: 1.25rem;
            color: #ffffff;
            padding: 1.5rem 2rem;
            box-shadow: 0 10px 25px -5px rgba(15, 23, 42, 0.25);
        }

        /* Print Media Styles */
        @media print {
            .no-print {
                display: none !important;
            }
            body {
                background: #ffffff !important;
                background-image: none !important;
                padding: 0 !important;
            }
            .invoice-card {
                box-shadow: none !important;
                border: 1px solid #cbd5e1 !important;
                border-radius: 0 !important;
            }
        }

        .btn-pill {
            border-radius: 9999px;
            font-weight: 600;
            padding: 0.55rem 1.35rem;
            font-size: 0.875rem;
            transition: all 0.2s ease;
        }
    </style>
</head>
<body>

    <!-- Floating Island Navbar (Hidden when printing) -->
    <header class="container pt-3 no-print">
        <nav class="navbar floating-nav px-4 py-2 mx-auto">
            <a class="navbar-brand d-flex align-items-center gap-2 fw-bold text-dark mb-0" href="${pageContext.request.contextPath}/orders">
                <span class="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle" style="width: 32px; height: 32px;">
                    <i class="bi bi-book-half" style="font-size: 0.95rem;"></i>
                </span>
                <span>PageCraft <span class="badge bg-indigo-subtle text-primary fw-medium small ms-1" style="font-size: 0.7rem; background-color: #e0e7ff;">Store</span></span>
            </a>
            <div class="d-flex align-items-center gap-2">
                <a class="nav-pill-link" href="${pageContext.request.contextPath}/orders">
                    <i class="bi bi-receipt me-1"></i>Orders
                </a>
                <a class="nav-pill-link" href="${pageContext.request.contextPath}/orders/new">
                    <i class="bi bi-plus-circle me-1"></i>New Order
                </a>
            </div>
        </nav>
    </header>

    <main class="container py-4">
        <div class="row justify-content-center">
            <div class="col-lg-9">

                <!-- Navigation Bar with Print, Edit, Delete -->
                <div class="d-flex flex-wrap align-items-center justify-content-between mb-4 gap-2 no-print">
                    <a href="${pageContext.request.contextPath}/orders" class="text-decoration-none text-muted small fw-semibold">
                        <i class="bi bi-arrow-left me-1"></i>Return to Registry
                    </a>
                    <div class="d-flex gap-2">
                        <button type="button" onclick="window.print();" class="btn btn-pill btn-outline-dark">
                            <i class="bi bi-printer me-1"></i>Print Invoice
                        </button>
                        <a href="${pageContext.request.contextPath}/orders/edit/${order.orderId}" class="btn btn-pill btn-outline-warning text-dark">
                            <i class="bi bi-pencil me-1"></i>Edit
                        </a>
                        <a href="${pageContext.request.contextPath}/orders/delete/${order.orderId}" 
                           class="btn btn-pill btn-outline-danger" 
                           onclick="return confirm('Confirm deletion of this order?');">
                            <i class="bi bi-trash3 me-1"></i>Delete
                        </a>
                    </div>
                </div>

                <!-- Executive Invoice Card -->
                <div class="invoice-card p-4 p-md-5">
                    <!-- Invoice Header & Branding -->
                    <div class="d-flex flex-wrap justify-content-between align-items-center pb-4 border-bottom mb-4 gap-3">
                        <div>
                            <div class="d-flex align-items-center gap-2 mb-2">
                                <span class="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-3 p-2">
                                    <i class="bi bi-book-half fs-4"></i>
                                </span>
                                <div>
                                    <h4 class="fw-bold text-dark mb-0">PageCraft Bookstore</h4>
                                    <small class="text-muted">Official Retail Sales Invoice</small>
                                </div>
                            </div>
                        </div>
                        <div class="text-md-end font-mono">
                            <span class="badge bg-dark rounded-pill px-3 py-2 fs-6 mb-1">
                                ${order.orderId}
                            </span>
                            <div class="text-muted small">
                                Transaction Date: <strong class="text-dark">${order.orderDate}</strong>
                            </div>
                        </div>
                    </div>

                    <!-- Customer & Transaction Details Grid -->
                    <div class="invoice-meta-box mb-4">
                        <div class="row g-3">
                            <div class="col-sm-6 col-md-4">
                                <span class="text-muted small text-uppercase fw-semibold d-block font-mono" style="font-size: 0.7rem;">Customer Name</span>
                                <span class="fw-bold text-dark fs-6">
                                    <i class="bi bi-person text-secondary me-1"></i>${order.customerName}
                                </span>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <span class="text-muted small text-uppercase fw-semibold d-block font-mono" style="font-size: 0.7rem;">Ordered Item</span>
                                <span class="fw-bold text-dark fs-6">
                                    <i class="bi bi-book text-primary me-1"></i>${order.bookName}
                                </span>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <span class="text-muted small text-uppercase fw-semibold d-block font-mono" style="font-size: 0.7rem;">Payment Method</span>
                                <span class="badge rounded-pill px-3 py-1 font-mono mt-1" style="background: #e2e8f0; color: #1e293b;">
                                    <i class="bi bi-credit-card-2-front me-1 text-primary"></i>${order.paymentMethod}
                                </span>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <span class="text-muted small text-uppercase fw-semibold d-block font-mono" style="font-size: 0.7rem;">Order Type</span>
                                <c:choose>
                                    <c:when test="${orderType == 'DISCOUNTED'}">
                                        <span class="badge rounded-pill px-3 py-1 font-mono mt-1" style="background: rgba(16, 185, 129, 0.15); color: #059669; border: 1px solid rgba(16, 185, 129, 0.3);">
                                            <i class="bi bi-tag-fill me-1"></i>DISCOUNTED (${discountRate}%)
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge rounded-pill px-3 py-1 font-mono mt-1" style="background: rgba(79, 70, 229, 0.15); color: #4338ca; border: 1px solid rgba(79, 70, 229, 0.3);">
                                            <i class="bi bi-shield-check me-1"></i>STANDARD (0%)
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <span class="text-muted small text-uppercase fw-semibold d-block font-mono" style="font-size: 0.7rem;">Status</span>
                                <span class="badge rounded-pill px-3 py-1 font-mono mt-1" style="background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0;">
                                    <i class="bi bi-check-circle-fill me-1"></i>PAID &amp; RECORDED
                                </span>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <span class="text-muted small text-uppercase fw-semibold d-block font-mono" style="font-size: 0.7rem;">Storage Reference</span>
                                <span class="font-mono small text-secondary">data/orders.txt</span>
                            </div>
                        </div>
                    </div>

                    <!-- Financial Line Item Breakdown Table -->
                    <div class="border rounded-4 overflow-hidden mb-4">
                        <table class="table table-invoice">
                            <thead>
                                <tr>
                                    <th scope="col">Description / Calculation Formula</th>
                                    <th scope="col" class="text-end" style="width: 220px;">Amount / Value</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>
                                        <div class="fw-semibold text-dark">Book Unit Price ($)</div>
                                        <small class="text-muted">Standard catalog retail rate per unit</small>
                                    </td>
                                    <td class="text-end font-mono fs-6">
                                        <fmt:formatNumber value="${order.bookPrice}" type="currency" currencySymbol="$" minFractionDigits="2" maxFractionDigits="2"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <div class="fw-semibold text-dark">Quantity</div>
                                        <small class="text-muted">Number of copies purchased</small>
                                    </td>
                                    <td class="text-end font-mono fs-6">
                                        ${order.quantity}
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <div class="fw-bold text-dark">Calculated Subtotal ($)</div>
                                        <small class="text-muted font-mono">Formula: Book Price × Quantity</small>
                                    </td>
                                    <td class="text-end font-mono fw-bold fs-6 text-dark">
                                        <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="$" minFractionDigits="2" maxFractionDigits="2"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <div class="fw-semibold text-dark">Applied Discount Amount ($)</div>
                                        <small class="text-muted">
                                            <c:choose>
                                                <c:when test="${orderType == 'DISCOUNTED'}">
                                                    Promotional Discount Rate: <strong>${discountRate}%</strong>
                                                </c:when>
                                                <c:otherwise>
                                                    Standard Order Policy: <strong>0% Discount</strong>
                                                </c:otherwise>
                                            </c:choose>
                                        </small>
                                    </td>
                                    <td class="text-end font-mono fs-6 text-danger fw-semibold">
                                        <c:choose>
                                            <c:when test="${discountAmount > 0}">
                                                - <fmt:formatNumber value="${discountAmount}" type="currency" currencySymbol="$" minFractionDigits="2" maxFractionDigits="2"/>
                                            </c:when>
                                            <c:otherwise>
                                                $0.00
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- Grand Total Banner -->
                    <div class="grand-total-banner d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
                        <div>
                            <span class="text-uppercase tracking-wider fw-bold text-success font-mono" style="font-size: 0.72rem; color: #34d399 !important;">
                                Net Total Due
                            </span>
                            <h4 class="fw-bold text-white mb-0">Final Amount Payable</h4>
                            <small class="text-white text-opacity-75">All taxes &amp; promotional rates included</small>
                        </div>
                        <div class="text-md-end">
                            <span class="display-6 fw-bold font-mono text-success d-block" style="color: #34d399 !important;">
                                <fmt:formatNumber value="${finalTotal}" type="currency" currencySymbol="$" minFractionDigits="2" maxFractionDigits="2"/>
                            </span>
                        </div>
                    </div>

                    <!-- Footer Notes -->
                    <div class="pt-3 border-top d-flex flex-wrap align-items-center justify-content-between text-muted small gap-2">
                        <div>
                            <i class="bi bi-shield-lock-fill text-success me-1"></i>Digitally confirmed transaction record from <strong>PageCraft Online Bookstore</strong>.
                        </div>
                        <div class="font-mono">
                            Verified on Java 26 • Spring Boot
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
