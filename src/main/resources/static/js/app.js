(function () {
    var sidebar = document.getElementById("sidebar");
    var btn = document.getElementById("toggleSidebar");

    if (!sidebar || !btn) return;

    // restore collapsed state
    var saved = localStorage.getItem("sidebar_collapsed");
    if (saved === "1") {
        sidebar.classList.add("is-collapsed");
        var icon = btn.querySelector("i");
        if (icon) icon.className = "bi bi-chevron-right";
    }

    btn.addEventListener("click", function () {
        var collapsed = sidebar.classList.toggle("is-collapsed");
        var icon = btn.querySelector("i");
        if (icon) icon.className = collapsed ? "bi bi-chevron-right" : "bi bi-chevron-left";
        localStorage.setItem("sidebar_collapsed", collapsed ? "1" : "0");
    });
})();
