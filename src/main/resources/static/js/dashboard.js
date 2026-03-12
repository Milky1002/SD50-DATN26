﻿(function () {
    const metrics = window.dashboardMetrics || {labels: [], current: [], previous: []};
    const chart = document.getElementById("revenueChart");
    const radios = document.querySelectorAll('input[name="revenueSeries"]');

    function maxValue(values) {
        if (!values || values.length === 0) {
            return 100;
        }
        return Math.max(...values, 10);
    }

    function drawLineChart(values) {
        if (!chart) {
            return;
        }

        const width = 760;
        const height = 280;
        const padding = {top: 20, right: 20, bottom: 36, left: 46};
        const innerWidth = width - padding.left - padding.right;
        const innerHeight = height - padding.top - padding.bottom;

        const labels = metrics.labels || [];
        const stepX = labels.length > 1 ? innerWidth / (labels.length - 1) : innerWidth;
        const ceiling = maxValue(values);

        const points = values.map((value, idx) => {
            const x = padding.left + idx * stepX;
            const y = padding.top + innerHeight - (value / ceiling) * innerHeight;
            return `${x},${y}`;
        });

        let gridY = "";
        for (let i = 0; i <= 3; i += 1) {
            const y = padding.top + (innerHeight / 3) * i;
            gridY += `<line x1="${padding.left}" y1="${y}" x2="${width - padding.right}" y2="${y}" stroke="#E7EAF4" stroke-width="1" />`;
        }

        let gridX = "";
        let xLabels = "";
        labels.forEach((label, idx) => {
            const x = padding.left + idx * stepX;
            gridX += `<line x1="${x}" y1="${padding.top}" x2="${x}" y2="${padding.top + innerHeight}" stroke="#F1F3F9" stroke-width="1" />`;
            xLabels += `<text x="${x}" y="${height - 10}" text-anchor="middle" font-size="12" fill="#6B7280">${label}</text>`;
        });

        const axis = `
            <line x1="${padding.left}" y1="${padding.top + innerHeight}" x2="${width - padding.right}" y2="${padding.top + innerHeight}" stroke="#D5D9E7" stroke-width="1" />
            <line x1="${padding.left}" y1="${padding.top}" x2="${padding.left}" y2="${padding.top + innerHeight}" stroke="#D5D9E7" stroke-width="1" />
        `;

        const polyline = points.length > 1
            ? `<polyline points="${points.join(" ")}" fill="none" stroke="#575787" stroke-width="2.8" stroke-linejoin="round" stroke-linecap="round"/>`
            : "";

        chart.innerHTML = `<g>${gridY}${gridX}${axis}${polyline}${xLabels}</g>`;
    }

    function drawDonut() {
        const donut = document.getElementById("operationDonut");
        if (!donut) {
            return;
        }

        const completed = Number(donut.dataset.completed || 0);
        const shipping = Number(donut.dataset.shipping || 0);
        const canceled = Number(donut.dataset.canceled || 0);
        const total = completed + shipping + canceled;

        if (total <= 0) {
            donut.style.background = "conic-gradient(#d1d5db 0deg 360deg)";
            return;
        }

        const completedDeg = (completed / total) * 360;
        const shippingDeg = (shipping / total) * 360;
        const canceledDeg = 360 - completedDeg - shippingDeg;

        donut.style.background = `conic-gradient(
            #22c55e 0deg ${completedDeg}deg,
            #5b5bd6 ${completedDeg}deg ${completedDeg + shippingDeg}deg,
            #ef4444 ${completedDeg + shippingDeg}deg ${completedDeg + shippingDeg + canceledDeg}deg
        )`;
    }

    drawLineChart(metrics.current || []);
    drawDonut();

    radios.forEach((radio) => {
        radio.addEventListener("change", function () {
            const values = this.value === "previous" ? (metrics.previous || []) : (metrics.current || []);
            drawLineChart(values);
        });
    });
})();
