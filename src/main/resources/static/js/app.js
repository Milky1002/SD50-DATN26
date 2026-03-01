(function () {
    const sidebar = document.getElementById("sidebar");
    const btn = document.getElementById("toggleSidebar");

    if (!sidebar || !btn) return;

    // restore
    const saved = localStorage.getItem("sidebar_collapsed");
    if (saved === "1") {
        sidebar.classList.add("is-collapsed");
        btn.textContent = "›";
    }

    btn.addEventListener("click", function () {
        const collapsed = sidebar.classList.toggle("is-collapsed");
        btn.textContent = collapsed ? "›" : "‹";
        localStorage.setItem("sidebar_collapsed", collapsed ? "1" : "0");
    });
})();