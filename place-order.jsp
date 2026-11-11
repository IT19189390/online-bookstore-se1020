<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${isEdit}">Edit Order</c:when><c:otherwise>Place Order</c:otherwise></c:choose> — BookStore Terminal</title>
    
    <!-- Google Fonts: Plus Jakarta Sans & JetBrains Mono -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600;700&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    
    <!-- Bootstrap 5 CSS & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    
    <!-- Select2 & Theme -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css" rel="stylesheet">

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

        /* Double-Bezel Card Container */
        .glass-shell {
            background: #ffffff;
            border: 1px solid var(--card-border);
            border-radius: 1.5rem;
            box-shadow: 0 20px 35px -10px rgba(15, 23, 42, 0.05), 0 1px 3px rgba(15, 23, 42, 0.04);
            overflow: hidden;
        }

        .card-header-styled {
            background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
            border-bottom: 1px solid var(--card-border);
            padding: 1.5rem 2rem;
        }

        /* Form Controls Styling */
        .form-label {
            font-size: 0.8125rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.04em;
            color: #334155;
            margin-bottom: 0.4rem;
        }

        .form-control, .form-select {
            font-size: 0.9375rem;
            border-radius: 0.75rem;
            border: 1px solid #cbd5e1;
            padding: 0.65rem 0.95rem;
            color: var(--text-heading);
            background-color: #ffffff;
            transition: all 0.2s ease;
        }

        .form-control:focus, .form-select:focus {
            border-color: #6366f1;
            box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15);
        }

        .input-group-text {
            background-color: #f1f5f9;
            border: 1px solid #cbd5e1;
            border-radius: 0.75rem;
            color: #64748b;
            font-weight: 600;
        }

        /* Locked Readonly Inputs */
        input[readonly] {
            background-color: #f8fafc !important;
            border: 1px solid #e2e8f0 !important;
            color: #1e293b !important;
            font-weight: 600;
            cursor: not-allowed;
        }

        /* Select2 Boost */
        .select2-container--bootstrap-5 .select2-selection {
            border-radius: 0.75rem !important;
            border: 1px solid #cbd5e1 !important;
            padding: 0.45rem 0.6rem !important;
            min-height: 44px !important;
        }
        .select2-container--bootstrap-5.select2-container--focus .select2-selection {
            border-color: #6366f1 !important;
            box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15) !important;
        }

        /* Dark High-End Live Calculation Card */
        .dark-breakdown-card {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 1.25rem;
            box-shadow: 0 15px 30px -8px rgba(15, 23, 42, 0.25);
            color: #f8fafc;
            padding: 1.5rem;
            position: relative;
            overflow: hidden;
        }

        .dark-breakdown-card::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(99, 102, 241, 0.12) 0%, transparent 70%);
            pointer-events: none;
        }

        .tile-surface {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(8px);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 1rem;
            padding: 1rem 0.75rem;
            transition: transform 0.2s ease;
        }

        .tile-surface:hover {
            transform: translateY(-2px);
            border-color: rgba(255, 255, 255, 0.18);
        }

        .tile-total-glow {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(5, 150, 105, 0.22) 100%);
            border: 1px solid rgba(16, 185, 129, 0.35);
        }

        /* Buttons */
        .btn-pill-primary {
            background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
            color: #ffffff;
            border: none;
            border-radius: 9999px;
            padding: 0.75rem 2rem;
            font-weight: 700;
            letter-spacing: 0.02em;
            box-shadow: 0 8px 18px -3px rgba(79, 70, 229, 0.35);
            transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .btn-pill-primary:hover {
            color: #ffffff;
            transform: translateY(-2px);
            box-shadow: 0 12px 24px -4px rgba(79, 70, 229, 0.45);
        }

        .btn-pill-secondary {
            border: 1px solid #cbd5e1;
            background: #ffffff;
            color: #475569;
            border-radius: 9999px;
            padding: 0.75rem 1.75rem;
            font-weight: 600;
            transition: all 0.2s ease;
        }

        .btn-pill-secondary:hover {
            background: #f1f5f9;
            color: #0f172a;
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
                <a class="nav-pill-link" href="${pageContext.request.contextPath}/orders">
                    <i class="bi bi-receipt me-1"></i>Orders
                </a>
                <a class="nav-pill-link active" href="${pageContext.request.contextPath}/orders/new">
                    <i class="bi bi-plus-circle-fill me-1"></i>New Order
                </a>
            </div>
        </nav>
    </header>

    <main class="container py-4">
        <div class="row justify-content-center">
            <div class="col-lg-9">

                <!-- Breadcrumb / Back Navigation -->
                <div class="d-flex align-items-center justify-content-between mb-3 px-1">
                    <a href="${pageContext.request.contextPath}/orders" class="text-decoration-none text-muted small fw-semibold">
                        <i class="bi bi-chevron-left me-1"></i>Back to Order Registry
                    </a>
                    <span class="text-muted small">
                        <i class="bi bi-shield-check text-success me-1"></i>Secure Checkout Terminal
                    </span>
                </div>

                <!-- Dismissible Top Validation Alert -->
                <div id="validationAlert" class="alert alert-danger border-0 shadow-sm rounded-4 alert-dismissible fade show d-none" role="alert">
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-shield-exclamation fs-5 text-danger"></i>
                        <div>
                            <strong>Form Validation Required:</strong>
                            <span id="validationMessage" class="ms-1 small">Please ensure all required fields are filled accurately.</span>
                        </div>
                    </div>
                    <button type="button" class="btn-close" aria-label="Close" onclick="dismissValidationAlert()"></button>
                </div>

                <!-- Main Glass Card Form -->
                <div class="glass-shell">
                    <div class="card-header-styled">
                        <div class="d-flex flex-wrap align-items-center justify-content-between gap-2">
                            <div>
                                <span class="badge rounded-pill text-uppercase tracking-wider px-3 py-1 mb-2 font-mono" style="font-size: 0.7rem; background: #e0e7ff; color: #4338ca;">
                                    Terminal Entry
                                </span>
                                <h3 class="fw-bold text-dark mb-0">
                                    <c:choose>
                                        <c:when test="${isEdit}">Modify Order Record</c:when>
                                        <c:otherwise>Create Customer Order</c:otherwise>
                                    </c:choose>
                                </h3>
                                <p class="text-muted small mb-0 mt-1">
                                    Book pricing and discount rules are securely verified by system catalog policies.
                                </p>
                            </div>
                            <c:if test="${isEdit}">
                                <div class="text-end font-mono">
                                    <span class="badge bg-secondary rounded-pill px-3 py-2 fs-6">
                                        ${order.orderId}
                                    </span>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <div class="p-4 p-md-5">
                        <form id="orderForm" action="${pageContext.request.contextPath}/orders/<c:choose><c:when test="${isEdit}">update</c:when><c:otherwise>save</c:otherwise></c:choose>" 
                              method="post" novalidate>

                            <!-- Hidden Order ID in Edit Mode -->
                            <c:if test="${isEdit}">
                                <input type="hidden" name="orderId" id="orderId" value="${order.orderId}">
                            </c:if>

                            <!-- Hidden Order Type driven by catalog rules -->
                            <input type="hidden" name="orderType" id="orderType" value="${orderType != null ? orderType : 'STANDARD'}">

                            <div class="row g-4">
                                <!-- Customer Full Name -->
                                <div class="col-md-6">
                                    <label for="customerName" class="form-label">Customer Name <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-person text-secondary"></i></span>
                                        <input type="text" class="form-control" id="customerName" name="customerName" 
                                               placeholder="e.g. Alice Smith" value="${order.customerName}">
                                    </div>
                                    <div class="invalid-feedback d-block text-danger small mt-1" id="customerNameFeedback" style="display:none !important;"></div>
                                </div>

                                <!-- Searchable Book Dropdown (Select2) -->
                                <div class="col-md-6">
                                    <label for="bookName" class="form-label">Book Title Selection <span class="text-danger">*</span></label>
                                    <select class="form-select select2-book" id="bookName" name="bookName" style="width: 100%;">
                                        <option value="" disabled ${empty order.bookName ? 'selected' : ''}>-- Search book title --</option>
                                        <c:forEach var="book" items="${bookCatalog}">
                                            <option value="${book.title}" 
                                                    data-price="${book.price}" 
                                                    data-discount="${book.discountRate}"
                                                    ${order.bookName == book.title ? 'selected' : ''}>
                                                ${book.title} — $<fmt:formatNumber value="${book.price}" pattern="0.00"/> (<c:choose><c:when test="${book.discountRate > 0}">${book.discountRate}% Promo</c:when><c:otherwise>Standard</c:otherwise></c:choose>)
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <div class="invalid-feedback d-block text-danger small mt-1" id="bookNameFeedback" style="display:none !important;"></div>
                                </div>

                                <!-- Quantity -->
                                <div class="col-md-4">
                                    <label for="quantity" class="form-label">Quantity <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-stack text-secondary"></i></span>
                                        <input type="number" min="1" step="1" class="form-control font-mono fw-bold" id="quantity" name="quantity" 
                                               placeholder="1" value="${order.quantity != null ? order.quantity : 1}">
                                    </div>
                                    <div class="invalid-feedback d-block text-danger small mt-1" id="quantityFeedback" style="display:none !important;"></div>
                                </div>

                                <!-- Locked Unit Price -->
                                <div class="col-md-4">
                                    <label for="bookPrice" class="form-label d-flex justify-content-between align-items-center">
                                        <span>Unit Price ($)</span>
                                        <span class="badge bg-light text-muted border py-0 px-1 font-mono" style="font-size: 0.65rem;">LOCKED</span>
                                    </label>
                                    <div class="input-group">
                                        <span class="input-group-text">$</span>
                                        <input type="number" step="0.01" class="form-control font-mono" id="bookPrice" name="bookPrice" 
                                               readonly value="${order.bookPrice != null ? order.bookPrice : '45.00'}">
                                    </div>
                                    <div class="form-text small text-muted">Fixed catalog unit price.</div>
                                </div>

                                <!-- Locked Discount Rate -->
                                <div class="col-md-4">
                                    <label for="discountRate" class="form-label d-flex justify-content-between align-items-center">
                                        <span>Discount Rate (%)</span>
                                        <span class="badge bg-light text-muted border py-0 px-1 font-mono" style="font-size: 0.65rem;">POLICY</span>
                                    </label>
                                    <div class="input-group">
                                        <input type="number" step="0.01" class="form-control font-mono" id="discountRate" name="discountRate" 
                                               readonly value="${discountRate != null ? discountRate : '10.0'}">
                                        <span class="input-group-text">%</span>
                                    </div>
                                    <div class="form-text small text-muted">Preset promotional discount.</div>
                                </div>

                                <!-- Payment Method -->
                                <div class="col-md-6">
                                    <label for="paymentMethod" class="form-label">Payment Method <span class="text-danger">*</span></label>
                                    <select class="form-select" id="paymentMethod" name="paymentMethod">
                                        <option value="" disabled ${empty order.paymentMethod ? 'selected' : ''}>-- Select payment provider --</option>
                                        <option value="Credit Card" ${order.paymentMethod == 'Credit Card' ? 'selected' : ''}>Credit Card (Stripe/Visa)</option>
                                        <option value="Debit Card" ${order.paymentMethod == 'Debit Card' ? 'selected' : ''}>Debit Card (Mastercard)</option>
                                        <option value="PayPal" ${order.paymentMethod == 'PayPal' ? 'selected' : ''}>PayPal Digital Wallet</option>
                                        <option value="Cash" ${order.paymentMethod == 'Cash' ? 'selected' : ''}>Cash on Delivery</option>
                                    </select>
                                    <div class="invalid-feedback d-block text-danger small mt-1" id="paymentMethodFeedback" style="display:none !important;"></div>
                                </div>

                                <!-- Order Date -->
                                <div class="col-md-6">
                                    <label for="orderDate" class="form-label">Order Date <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-calendar3 text-secondary"></i></span>
                                        <input type="date" class="form-control font-mono" id="orderDate" name="orderDate" 
                                               value="${order.orderDate}">
                                    </div>
                                    <div class="invalid-feedback d-block text-danger small mt-1" id="orderDateFeedback" style="display:none !important;"></div>
                                </div>
                            </div>

                            <!-- Real-Time Calculation Live Breakdown Box -->
                            <div class="dark-breakdown-card mt-5 mb-4">
                                <div class="d-flex flex-wrap align-items-center justify-content-between pb-3 border-bottom border-white border-opacity-10 mb-3 gap-2">
                                    <div>
                                        <span class="text-uppercase tracking-wider fw-bold text-indigo-300 font-mono" style="font-size: 0.72rem; color: #a5b4fc;">
                                            Live Order Dynamics
                                        </span>
                                        <h5 class="fw-bold mb-0 text-white">Calculation Breakdown</h5>
                                    </div>
                                    <div>
                                        <span id="orderTypeBadge" class="badge rounded-pill px-3 py-2 font-mono fs-7 bg-success bg-opacity-25 text-success border border-success border-opacity-25">
                                            DISCOUNTED ORDER
                                        </span>
                                    </div>
                                </div>

                                <div class="row g-3 text-center">
                                    <div class="col-md-4">
                                        <div class="tile-surface">
                                            <span class="text-uppercase small fw-semibold text-white text-opacity-50 d-block font-mono" style="font-size: 0.7rem;">Subtotal</span>
                                            <span class="fs-4 fw-bold text-white font-mono d-block my-1" id="previewSubtotal">$0.00</span>
                                            <small class="text-white text-opacity-50" style="font-size: 0.75rem;">(Price × Quantity)</small>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="tile-surface">
                                            <span class="text-uppercase small fw-semibold text-white text-opacity-50 d-block font-mono" style="font-size: 0.7rem;">Applied Deduction</span>
                                            <span class="fs-4 fw-bold text-danger font-mono d-block my-1" style="color: #f87171 !important;" id="previewDiscount">-$0.00</span>
                                            <small class="text-white text-opacity-50" style="font-size: 0.75rem;" id="previewDiscountLabel">(0% Rate)</small>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="tile-surface tile-total-glow">
                                            <span class="text-uppercase small fw-bold text-success d-block font-mono" style="font-size: 0.7rem; color: #34d399 !important;">Net Total Due</span>
                                            <span class="fs-3 fw-bold text-success font-mono d-block my-1" style="color: #34d399 !important;" id="previewTotal">$0.00</span>
                                            <small class="text-success text-opacity-75 fw-semibold" style="font-size: 0.75rem;">Payable Amount</small>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Actions Row -->
                            <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 pt-3 border-top">
                                <span class="text-muted small">
                                    <i class="bi bi-info-circle me-1"></i>All entries persist directly to <code>data/orders.txt</code>
                                </span>
                                <div class="d-flex gap-2">
                                    <a href="${pageContext.request.contextPath}/orders" class="btn btn-pill-secondary">
                                        Discard
                                    </a>
                                    <button type="submit" class="btn btn-pill-primary">
                                        <i class="bi <c:choose><c:when test="${isEdit}">bi-check2-circle</c:when><c:otherwise>bi-bag-plus</c:otherwise></c:choose> me-2"></i>
                                        <c:choose>
                                            <c:when test="${isEdit}">Commit Changes</c:when>
                                            <c:otherwise>Confirm &amp; Place Order</c:otherwise>
                                        </c:choose>
                                    </button>
                                </div>
                            </div>

                        </form>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <!-- jQuery & Select2 Scripts -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        const catalogRules = {
            "Effective Java": { price: 45.00, discount: 10.0 },
            "Clean Code": { price: 40.00, discount: 0.0 },
            "Design Patterns": { price: 50.00, discount: 15.0 },
            "Spring in Action": { price: 38.00, discount: 5.0 },
            "Java: The Complete Reference": { price: 55.00, discount: 0.0 }
        };

        $(document).ready(function() {
            const $bookSelect = $('#bookName').select2({
                theme: 'bootstrap-5',
                placeholder: '-- Search book title --',
                allowClear: false
            });

            $bookSelect.on('select2:select', function() {
                applyBookRules($(this).val());
                clearFieldError('bookName');
                recalculate();
            });

            $('#quantity').on('input change', function() {
                clearFieldError('quantity');
                recalculate();
            });

            const $dateField = $('#orderDate');
            if (!$dateField.val()) {
                const today = new Date().toISOString().split('T')[0];
                $dateField.val(today);
            }

            const initialBook = $bookSelect.val();
            if (initialBook && catalogRules[initialBook]) {
                applyBookRules(initialBook);
            } else if (!initialBook) {
                const defaultBook = "Effective Java";
                $bookSelect.val(defaultBook).trigger('change');
                applyBookRules(defaultBook);
            }
            recalculate();

            $('#orderForm').on('submit', function(e) {
                if (!validateOrderForm()) {
                    e.preventDefault();
                    return false;
                }
            });
        });

        function applyBookRules(bookTitle) {
            if (!bookTitle) return;
            const rule = catalogRules[bookTitle];
            if (rule) {
                $('#bookPrice').val(rule.price.toFixed(2));
                $('#discountRate').val(rule.discount.toFixed(1));

                const $badge = $('#orderTypeBadge');
                if (rule.discount > 0) {
                    $('#orderType').val('DISCOUNTED');
                    $badge.removeClass('bg-primary text-primary').addClass('bg-success text-success')
                          .html('<i class="bi bi-tag-fill me-1"></i>DISCOUNTED (' + rule.discount.toFixed(0) + '% PROMO)');
                } else {
                    $('#orderType').val('STANDARD');
                    $badge.removeClass('bg-success text-success').addClass('bg-primary text-primary')
                          .html('<i class="bi bi-shield-check me-1"></i>STANDARD (0% DISCOUNT)');
                }
            }
        }

        function recalculate() {
            const quantity = parseInt($('#quantity').val()) || 0;
            const unitPrice = parseFloat($('#bookPrice').val()) || 0.0;
            const discountRate = parseFloat($('#discountRate').val()) || 0.0;

            const subtotal = unitPrice * quantity;
            const discountAmount = subtotal * (discountRate / 100.0);
            const totalPayable = Math.max(0.0, subtotal - discountAmount);

            $('#previewSubtotal').text('$' + subtotal.toFixed(2));
            $('#previewDiscount').text('-$' + discountAmount.toFixed(2));
            $('#previewDiscountLabel').text('(' + discountRate.toFixed(1) + '% Rate)');
            $('#previewTotal').text('$' + totalPayable.toFixed(2));
        }

        function validateOrderForm() {
            let isValid = true;
            let errors = [];

            const customerName = $('#customerName').val().trim();
            if (!customerName || customerName.length < 2) {
                showFieldError('customerName', 'Please enter a valid customer name (at least 2 characters).');
                errors.push('Customer Name must be at least 2 characters.');
                isValid = false;
            } else {
                clearFieldError('customerName');
            }

            const bookName = $('#bookName').val();
            if (!bookName || bookName.trim() === '') {
                showFieldError('bookName', 'Please select a book from the catalog.');
                errors.push('Book selection is required.');
                isValid = false;
            } else {
                clearFieldError('bookName');
            }

            const qtyVal = $('#quantity').val();
            const qty = parseInt(qtyVal, 10);
            if (!qtyVal || isNaN(qty) || qty <= 0) {
                showFieldError('quantity', 'Quantity must be an integer of 1 or more.');
                errors.push('Quantity must be greater than 0.');
                isValid = false;
            } else {
                clearFieldError('quantity');
            }

            const paymentMethod = $('#paymentMethod').val();
            if (!paymentMethod) {
                showFieldError('paymentMethod', 'Please specify a payment method.');
                errors.push('Payment Method is required.');
                isValid = false;
            } else {
                clearFieldError('paymentMethod');
            }

            const orderDate = $('#orderDate').val();
            if (!orderDate) {
                showFieldError('orderDate', 'Order date is required.');
                errors.push('Order Date is required.');
                isValid = false;
            } else {
                clearFieldError('orderDate');
            }

            if (!isValid) {
                $('#validationMessage').text(errors.join(' • '));
                $('#validationAlert').removeClass('d-none');
                $('html, body').animate({ scrollTop: 0 }, 200);
            } else {
                $('#validationAlert').addClass('d-none');
            }

            return isValid;
        }

        function showFieldError(fieldId, message) {
            $('#' + fieldId).addClass('is-invalid').removeClass('is-valid');
            $('#' + fieldId + 'Feedback').text(message).css('display', 'block');
        }

        function clearFieldError(fieldId) {
            $('#' + fieldId).removeClass('is-invalid').addClass('is-valid');
            $('#' + fieldId + 'Feedback').text('').css('display', 'none');
        }

        function dismissValidationAlert() {
            $('#validationAlert').addClass('d-none');
        }
    </script>
</body>
</html>
