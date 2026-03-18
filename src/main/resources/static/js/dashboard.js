(function () {
    var metrics = window.dashboardMetrics || {labels: [], current: [], previous: []};
    var chart = document.getElementById("revenueChart");
    var radios = document.querySelectorAll('input[name="revenueSeries"]');

    function maxValue(values) {
        if (!values || values.length === 0) return 100;
        return Math.max.apply(null, values.concat([10]));
    }

    function drawLineChart(values) {
        if (!chart) return;

        var width = 760;
        var height = 280;
        var padding = {top: 20, right: 20, bottom: 36, left: 60};
        var innerWidth = width - padding.left - padding.right;
        var innerHeight = height - padding.top - padding.bottom;

        var labels = metrics.labels || [];
        var stepX = labels.length > 1 ? innerWidth / (labels.length - 1) : innerWidth;
        var ceiling = maxValue(values);

        var points = [];
        for (var i = 0; i < values.length; i++) {
            var x = padding.left + i * stepX;
            var y = padding.top + innerHeight - (values[i] / ceiling) * innerHeight;
            points.push(x + "," + y);
        }

        var gridY = "";
        for (var j = 0; j <= 4; j++) {
            var gy = padding.top + (innerHeight / 4) * j;
            var labelVal = Math.round(ceiling - (ceiling / 4) * j);
            gridY += '<line x1="' + padding.left + '" y1="' + gy + '" x2="' + (width - padding.right) + '" y2="' + gy + '" stroke="#e2e8f0" stroke-width="1" />';
            gridY += '<text x="' + (padding.left - 8) + '" y="' + (gy + 4) + '" text-anchor="end" font-size="11" fill="#94a3b8">' + labelVal.toLocaleString('vi-VN') + '</text>';
        }

        var xLabels = "";
        for (var k = 0; k < labels.length; k++) {
            var lx = padding.left + k * stepX;
            xLabels += '<text x="' + lx + '" y="' + (height - 8) + '" text-anchor="middle" font-size="11" fill="#94a3b8">' + labels[k] + '</text>';
        }

        // Gradient area fill
        var areaPath = "";
        if (points.length > 1) {
            areaPath = '<defs><linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">'
                + '<stop offset="0%" stop-color="#4f46e5" stop-opacity="0.15"/>'
                + '<stop offset="100%" stop-color="#4f46e5" stop-opacity="0.01"/>'
                + '</linearGradient></defs>';
            var firstPt = points[0].split(",");
            var lastPt = points[points.length - 1].split(",");
            areaPath += '<polygon points="' + firstPt[0] + ',' + (padding.top + innerHeight) + ' '
                + points.join(" ") + ' '
                + lastPt[0] + ',' + (padding.top + innerHeight) + '" fill="url(#areaGrad)"/>';
        }

        var polyline = points.length > 1
            ? '<polyline points="' + points.join(" ") + '" fill="none" stroke="#4f46e5" stroke-width="2.5" stroke-linejoin="round" stroke-linecap="round"/>'
            : "";

        // Data points
        var dots = "";
        for (var d = 0; d < points.length; d++) {
            var pt = points[d].split(",");
            dots += '<circle cx="' + pt[0] + '" cy="' + pt[1] + '" r="3.5" fill="#4f46e5" stroke="#fff" stroke-width="2"/>';
        }

        chart.innerHTML = '<g>' + areaPath + gridY + xLabels + polyline + dots + '</g>';
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

    drawLineChart(metrics.current || []);
    drawDonut();

    radios.forEach(function(radio) {
        radio.addEventListener("change", function () {
            var values = this.value === "previous" ? (metrics.previous || []) : (metrics.current || []);
            drawLineChart(values);
        });
    });
})();
