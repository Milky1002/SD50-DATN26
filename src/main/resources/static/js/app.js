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

/* ===== USER MENU DROPDOWN ===== */
(function () {
    var menuBtn = document.getElementById("userMenuBtn");
    var dropdown = document.getElementById("userMenuDropdown");
    if (!menuBtn || !dropdown) return;

    menuBtn.addEventListener("click", function (e) {
        e.stopPropagation();
        dropdown.classList.toggle("is-open");
    });

    document.addEventListener("click", function (e) {
        if (!dropdown.contains(e.target) && e.target !== menuBtn) {
            dropdown.classList.remove("is-open");
        }
    });
})();

/* ===== TOAST NOTIFICATIONS ===== */
function showToast(message, type) {
    type = type || "success";
    var container = document.getElementById("toastContainer");
    if (!container) return;

    var icon = type === "success" ? "bi-check-circle-fill" :
               type === "error" ? "bi-exclamation-triangle-fill" :
               "bi-info-circle-fill";

    var toast = document.createElement("div");
    toast.className = "toast toast--" + type;
    toast.innerHTML = '<i class="bi ' + icon + '"></i> ' + message;
    container.appendChild(toast);

    setTimeout(function () {
        toast.style.opacity = "0";
        toast.style.transform = "translateY(-10px)";
        toast.style.transition = "all .3s ease";
        setTimeout(function () { toast.remove(); }, 300);
    }, 3000);
}

/* ===== CHANGE PASSWORD MODAL ===== */
function openChangePasswordModal() {
    // Close user menu dropdown
    var dropdown = document.getElementById("userMenuDropdown");
    if (dropdown) dropdown.classList.remove("is-open");

    var overlay = document.getElementById("changePasswordOverlay");
    if (overlay) {
        overlay.style.display = "flex";
        // Clear fields
        document.getElementById("cpOldPwd").value = "";
        document.getElementById("cpNewPwd").value = "";
        document.getElementById("cpConfirmPwd").value = "";
        var msg = document.getElementById("cpMsg");
        if (msg) { msg.style.display = "none"; msg.textContent = ""; }
    }
}

function closeChangePasswordModal() {
    var overlay = document.getElementById("changePasswordOverlay");
    if (overlay) overlay.style.display = "none";
}

function submitChangePassword() {
    var oldPwd = document.getElementById("cpOldPwd").value.trim();
    var newPwd = document.getElementById("cpNewPwd").value.trim();
    var confirmPwd = document.getElementById("cpConfirmPwd").value.trim();
    var msgEl = document.getElementById("cpMsg");

    // Validate
    if (!oldPwd || !newPwd || !confirmPwd) {
        showCpMsg("Vui lòng nhập đầy đủ thông tin", "error");
        return;
    }
    if (newPwd.length < 6) {
        showCpMsg("Mật khẩu mới phải có ít nhất 6 ký tự", "error");
        return;
    }
    if (newPwd !== confirmPwd) {
        showCpMsg("Xác nhận mật khẩu không khớp", "error");
        return;
    }

    fetch("/api/change-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ oldPassword: oldPwd, newPassword: newPwd })
    })
    .then(function (res) { return res.json(); })
    .then(function (data) {
        if (data.success) {
            closeChangePasswordModal();
            showToast("Đổi mật khẩu thành công!", "success");
        } else {
            showCpMsg(data.message || "Đổi mật khẩu thất bại", "error");
        }
    })
    .catch(function () {
        showCpMsg("Lỗi kết nối, vui lòng thử lại", "error");
    });
}

function showCpMsg(message, type) {
    var msgEl = document.getElementById("cpMsg");
    if (!msgEl) return;
    msgEl.textContent = message;
    msgEl.className = "cp-msg cp-msg--" + type;
    msgEl.style.display = "block";
}

/* ===== ACCESS DENIED TOAST ===== */
(function () {
    var params = new URLSearchParams(window.location.search);
    if (params.get("error") === "access_denied") {
        showToast("Bạn không có quyền truy cập chức năng này", "error");
        // Clean URL
        var url = new URL(window.location);
        url.searchParams.delete("error");
        window.history.replaceState({}, "", url);
    }
})();
