(function () {
    const rows = Array.from(document.querySelectorAll("#orderTableBody tr"));
    const tabs = Array.from(document.querySelectorAll("[data-order-tab]"));

    function filterByTab(status) {
        rows.forEach(function (row) {
            const rowStatus = row.getAttribute("data-status") || "all";
            const visible = !status || status === "all" || rowStatus === status;
            row.style.display = visible ? "" : "none";
        });
    }

    if (tabs.length) {
        tabs.forEach(function (tab) {
            tab.addEventListener("click", function () {
                const status = tab.getAttribute("data-order-tab");
                tabs.forEach(function (t) {
                    t.classList.toggle("is-active", t === tab);
                });
                filterByTab(status);
            });
        });
    }
})();
