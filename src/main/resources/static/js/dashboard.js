(function () {
    var metrics = window.dashboardMetrics || {};
    var chart = document.getElementById("revenueChart");
    var radios = document.querySelectorAll('input[name="revenueSeries"]');
    var salesRangeSelect = document.getElementById("salesRangeSelect");
    var salesDateLabel = document.querySelector(".sales-panel .panel-note strong");
    var revenueRangeSelect = document.getElementById("revenueRangeSelect");
    var revenueCurrentRange = document.getElementById("revenueCurrentRange");
    var revenuePreviousRange = document.getElementById("revenuePreviousRange");
    var revenueLegendLabels = document.querySelectorAll(".revenue-legend label");
    var revenuePagination = document.getElementById("revenuePagination");
    var revenuePageSize = 15;
    var revenueCurrentPage = 1;

    function parseDate(dateText) {
        var parts = (dateText || "").split("/");
        if (parts.length !== 3) return null;
        return new Date(Number(parts[2]), Number(parts[1]) - 1, Number(parts[0]));
    }

    function formatDate(date) {
        var day = String(date.getDate()).padStart(2, "0");
        var month = String(date.getMonth() + 1).padStart(2, "0");
        var year = date.getFullYear();
        return day + "/" + month + "/" + year;
    }

    function buildSales30DayLabel(default7DayLabel) {
        var parts = (default7DayLabel || "").split(" - ");
        if (parts.length !== 2) return default7DayLabel || "";
        var toDate = parseDate(parts[1]);
        if (!toDate) return default7DayLabel || "";
        var fromDate = new Date(toDate);
        fromDate.setDate(toDate.getDate() - 29);
        return formatDate(fromDate) + " - " + formatDate(toDate);
    }

    function setupSalesRange() {
        if (!salesRangeSelect || !salesDateLabel) return;

        var sales7DayLabel = metrics.sales7DayLabel || salesDateLabel.textContent.trim();
        var sales30DayLabel = metrics.sales30DayLabel || buildSales30DayLabel(sales7DayLabel);

        salesRangeSelect.innerHTML =
            '<option value="7days">7 ngày gần đây</option>' +
            '<option value="30days">30 ngày gần đây</option>';
        salesRangeSelect.value = "7days";
        salesDateLabel.textContent = sales7DayLabel;

        salesRangeSelect.addEventListener("change", function () {
            salesDateLabel.textContent = this.value === "30days" ? sales30DayLabel : sales7DayLabel;
        });
    }

    function getRevenueDatasetKey() {
        return revenueRangeSelect && revenueRangeSelect.value === "30days" ? "30days" : "7days";
    }

    function getSelectedRevenueSeries() {
        var checked = document.querySelector('input[name="revenueSeries"]:checked');
        return checked && checked.value === "previous" ? "previous" : "current";
    }

    function getRevenueDataset() {
        var rangeKey = getRevenueDatasetKey();
        return {
            currentLabels: rangeKey === "30days" ? (metrics.revenue30DaysCurrentLabels || []) : (metrics.revenue7DaysCurrentLabels || []),
            previousLabels: rangeKey === "30days" ? (metrics.revenue30DaysPreviousLabels || []) : (metrics.revenue7DaysPreviousLabels || []),
            currentValues: rangeKey === "30days" ? (metrics.revenue30DaysCurrentValues || []) : (metrics.revenue7DaysCurrentValues || []),
            previousValues: rangeKey === "30days" ? (metrics.revenue30DaysPreviousValues || []) : (metrics.revenue7DaysPreviousValues || []),
            currentRange: rangeKey === "30days" ? (metrics.revenue30DaysCurrentRange || "") : (metrics.revenue7DaysCurrentRange || ""),
            previousRange: rangeKey === "30days" ? (metrics.revenue30DaysPreviousRange || "") : (metrics.revenue7DaysPreviousRange || ""),
            currentLabelText: rangeKey === "30days" ? "30 ngày gần đây" : "7 ngày gần đây"
        };
    }

    function syncRevenueLegend() {
        var dataset = getRevenueDataset();
        if (revenueCurrentRange) revenueCurrentRange.textContent = dataset.currentRange;
        if (revenuePreviousRange) revenuePreviousRange.textContent = dataset.previousRange;
        if (revenueLegendLabels.length > 0) {
            var currentTextNode = Array.from(revenueLegendLabels[0].childNodes).find(function (node) {
                return node.nodeType === Node.TEXT_NODE && node.textContent.trim().length > 0;
            });
            if (currentTextNode) currentTextNode.textContent = " " + dataset.currentLabelText + " ";
        }
    }

    function getRevenuePagedSeries(dataset, selectedSeries) {
        var labels = selectedSeries === "previous" ? dataset.previousLabels : dataset.currentLabels;
        var values = selectedSeries === "previous" ? dataset.previousValues : dataset.currentValues;
        var totalPages = Math.max(1, Math.ceil(labels.length / revenuePageSize));
        revenueCurrentPage = Math.min(revenueCurrentPage, totalPages);

        var start = (revenueCurrentPage - 1) * revenuePageSize;
        var end = start + revenuePageSize;

        return {
            labels: labels.slice(start, end),
            values: values.slice(start, end),
            totalPages: totalPages
        };
    }

    function renderRevenuePagination(totalPages) {
        if (!revenuePagination) return;

        if (totalPages <= 1) {
            revenuePagination.classList.add("is-hidden");
            revenuePagination.innerHTML = "";
            return;
        }

        revenuePagination.classList.remove("is-hidden");
        revenuePagination.innerHTML =
            '<button class="revenue-page-btn" type="button" data-page="prev"' + (revenueCurrentPage === 1 ? ' disabled' : '') + '>' +
            '<i class="bi bi-chevron-left"></i></button>' +
            '<span class="revenue-page-info">Trang ' + revenueCurrentPage + '/' + totalPages + '</span>' +
            '<button class="revenue-page-btn" type="button" data-page="next"' + (revenueCurrentPage === totalPages ? ' disabled' : '') + '>' +
            '<i class="bi bi-chevron-right"></i></button>';

        revenuePagination.querySelectorAll(".revenue-page-btn").forEach(function (button) {
            button.addEventListener("click", function () {
                var direction = this.getAttribute("data-page");
                if (direction === "prev" && revenueCurrentPage > 1) revenueCurrentPage--;
                if (direction === "next" && revenueCurrentPage < totalPages) revenueCurrentPage++;
                drawSelectedRevenueSeries();
            });
        });
    }

    function drawSelectedRevenueSeries() {
        var dataset = getRevenueDataset();
        var selectedSeries = getSelectedRevenueSeries();
        var pagedSeries = getRevenuePagedSeries(dataset, selectedSeries);
        drawBarChart(
            pagedSeries.values,
            pagedSeries.labels
        );
        syncRevenueLegend();
        renderRevenuePagination(pagedSeries.totalPages);
    }

    function maxValue(values) {
        if (!values || values.length === 0) return 100;
        return Math.max.apply(null, values.concat([10]));
    }

    function drawBarChart(values, labels) {
        if (!chart) return;

        var width = Math.max(chart.clientWidth || 0, 760);
        var height = Math.max(chart.clientHeight || 0, 280);
        var padding = {top: 30, right: 20, bottom: 42, left: 72};
        var innerWidth = width - padding.left - padding.right;
        var innerHeight = height - padding.top - padding.bottom;
        labels = labels || [];
        var count = Math.max(labels.length, values.length, 1);
        var ceiling = maxValue(values);
        var slotWidth = innerWidth / count;
        var barWidth = count === 1
            ? Math.min(72, innerWidth * 0.16)
            : Math.min(56, Math.max(24, slotWidth * 0.6));

        var gridY = "";
        for (var j = 0; j <= 4; j++) {
            var gy = padding.top + (innerHeight / 4) * j;
            var labelVal = Math.round(ceiling - (ceiling / 4) * j);
            gridY += '<line x1="' + padding.left + '" y1="' + gy + '" x2="' + (width - padding.right)
                + '" y2="' + gy + '" stroke="#e2e8f0" stroke-width="1" />';
            gridY += '<text x="' + (padding.left - 10) + '" y="' + (gy + 4)
                + '" text-anchor="end" font-size="11" fill="#94a3b8">' + labelVal.toLocaleString("vi-VN") + '</text>';
        }

        var xLabels = "";
        var bars = '<defs><linearGradient id="barGrad" x1="0" y1="0" x2="0" y2="1">'
            + '<stop offset="0%" stop-color="#7c83ff"/>'
            + '<stop offset="100%" stop-color="#4f46e5"/>'
            + '</linearGradient></defs>';

        for (var i = 0; i < count; i++) {
            var value = Number(values[i] || 0);
            var x = padding.left + slotWidth * i + (slotWidth - barWidth) / 2;
            var barHeight = ceiling > 0 ? (value / ceiling) * innerHeight : 0;
            var y = padding.top + innerHeight - barHeight;
            var labelX = padding.left + slotWidth * i + slotWidth / 2;
            var textY = Math.max(18, y - 8);

            xLabels += '<text x="' + labelX + '" y="' + (height - 10)
                + '" text-anchor="middle" font-size="11" font-weight="600" fill="#64748b">'
                + (labels[i] || "") + '</text>';

            if (value <= 0) {
                bars += '<rect x="' + x + '" y="' + (padding.top + innerHeight - 4)
                    + '" width="' + barWidth + '" height="4" rx="2" ry="2" fill="#cbd5e1"/>';
                continue;
            }

            bars += '<rect x="' + x + '" y="' + y + '" width="' + barWidth + '" height="' + barHeight
                + '" rx="12" ry="12" fill="url(#barGrad)"/>';
            bars += '<text x="' + labelX + '" y="' + textY
                + '" text-anchor="middle" font-size="11" font-weight="700" fill="#475569">'
                + value.toLocaleString("vi-VN") + '</text>';
        }

        chart.setAttribute("viewBox", "0 0 " + width + " " + height);
        chart.innerHTML = '<g>' + gridY + xLabels + bars + '</g>';
    }

    function drawDonut() {
        var donut = document.getElementById("operationDonut");
        if (!donut) return;

        var completed = Number(donut.dataset.completed || 0);
        var shipping = Number(donut.dataset.shipping || 0);
        var canceled = Number(donut.dataset.canceled || 0);
        var total = completed + shipping + canceled;

        if (total <= 0) {
            donut.style.background = "conic-gradient(#e2e8f0 0deg 360deg)";
            return;
        }

        var completedDeg = (completed / total) * 360;
        var shippingDeg = (shipping / total) * 360;
        var canceledDeg = 360 - completedDeg - shippingDeg;

        donut.style.background = "conic-gradient("
            + "#059669 0deg " + completedDeg + "deg, "
            + "#4f46e5 " + completedDeg + "deg " + (completedDeg + shippingDeg) + "deg, "
            + "#dc2626 " + (completedDeg + shippingDeg) + "deg " + (completedDeg + shippingDeg + canceledDeg) + "deg)";
    }

    if (revenueRangeSelect) {
        revenueRangeSelect.innerHTML =
            '<option value="7days">7 ngày gần đây</option>' +
            '<option value="30days">30 ngày gần đây</option>';
        revenueRangeSelect.value = "7days";
    }

    drawSelectedRevenueSeries();
    drawDonut();
    setupSalesRange();

    window.addEventListener("resize", function () {
        drawSelectedRevenueSeries();
    });

    radios.forEach(function (radio) {
        radio.addEventListener("change", function () {
            revenueCurrentPage = 1;
            drawSelectedRevenueSeries();
        });
    });

    if (revenueRangeSelect) {
        revenueRangeSelect.addEventListener("change", function () {
            revenueCurrentPage = 1;
            drawSelectedRevenueSeries();
        });
    }
})();
