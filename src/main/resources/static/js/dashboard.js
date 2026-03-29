(function () {
    var metrics = window.dashboardMetrics || {labels: [], current: [], previous: []};
    var chart = document.getElementById("revenueChart");
    var radios = document.querySelectorAll('input[name="revenueSeries"]');

    function maxValue(values) {
        if (!values || values.length === 0) return 100;
        return Math.max.apply(null, values.concat([10]));
    }

    function drawBarChart(values) {
        if (!chart) return;

        var width = 760;
        var height = 280;
        var padding = {top: 30, right: 20, bottom: 42, left: 72};
        var innerWidth = width - padding.left - padding.right;
        var innerHeight = height - padding.top - padding.bottom;
        var labels = metrics.labels || [];
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

    drawBarChart(metrics.current || []);
    drawDonut();

    radios.forEach(function (radio) {
        radio.addEventListener("change", function () {
            var values = this.value === "previous" ? (metrics.previous || []) : (metrics.current || []);
            drawBarChart(values);
        });
    });
})();
