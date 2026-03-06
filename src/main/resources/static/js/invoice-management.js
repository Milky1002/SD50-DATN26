(function () {
    const searchInput = document.getElementById("invoiceSearchInput");
    const statusFilter = document.getElementById("invoiceStatusFilter");
    const dateFrom = document.getElementById("invoiceDateFrom");
    const dateTo = document.getElementById("invoiceDateTo");
    const resetBtn = document.getElementById("invoiceFilterReset");
    const rows = Array.from(document.querySelectorAll("#invoiceTableBody tr"));

    function normalize(str) {
        return (str || "").toString().toLowerCase().trim();
    }

    function applyFilter() {
        const q = normalize(searchInput && searchInput.value);
        const statusVal = statusFilter && statusFilter.value;
        const from = dateFrom && dateFrom.value ? new Date(dateFrom.value) : null;
        const to = dateTo && dateTo.value ? new Date(dateTo.value) : null;

        rows.forEach(function (row) {
            const code = row.querySelector(".invoice-code")?.textContent || "";
            const name = row.querySelector(".invoice-row__name")?.textContent || "";
            const phone = row.querySelector(".invoice-row__sub")?.textContent || "";
            const statusText = row.getAttribute("data-status") || "";
            const dateAttr = row.querySelector("[data-date]");
            const d = dateAttr ? new Date(dateAttr.getAttribute("data-date")) : null;

            let visible = true;

            if (q) {
                const haystack = normalize(code + " " + name + " " + phone);
                if (!haystack.includes(q)) {
                    visible = false;
                }
            }

            if (visible && statusVal) {
                if (statusVal === "paid" && statusText !== "Đã thanh toán") {
                    visible = false;
                } else if (statusVal === "unpaid" && statusText !== "Chưa thanh toán") {
                    visible = false;
                } else if (statusVal === "partial" && statusText !== "T.toán một phần") {
                    visible = false;
                }
            }

            if (visible && from && d && d < from) {
                visible = false;
            }

            if (visible && to && d && d > to) {
                visible = false;
            }

            row.style.display = visible ? "" : "none";
        });
    }

    if (searchInput) searchInput.addEventListener("input", applyFilter);
    if (statusFilter) statusFilter.addEventListener("change", applyFilter);
    if (dateFrom) dateFrom.addEventListener("change", applyFilter);
    if (dateTo) dateTo.addEventListener("change", applyFilter);
    if (resetBtn) {
        resetBtn.addEventListener("click", function () {
            if (searchInput) searchInput.value = "";
            if (statusFilter) statusFilter.value = "";
            if (dateFrom) dateFrom.value = "";
            if (dateTo) dateTo.value = "";
            applyFilter();
        });
    }
})();

