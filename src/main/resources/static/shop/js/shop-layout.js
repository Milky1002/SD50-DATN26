(function () {
    const body = document.body;
    const header = document.querySelector('[data-sticky-header]');
    const mobileToggle = document.querySelector('[data-mobile-nav-toggle]');
    const mobilePanel = document.querySelector('[data-mobile-nav-panel]');
    const cartBadge = document.querySelector('[data-cart-badge]');
    const toastContainer = document.getElementById('shopToastContainer');

    function toggleScrolledState() {
        if (!header) {
            return;
        }

        header.classList.toggle('shop-header--scrolled', window.scrollY > 12);
    }

    function setMobileNavOpen(isOpen) {
        if (!mobileToggle || !mobilePanel) {
            return;
        }

        mobileToggle.setAttribute('aria-expanded', String(isOpen));
        mobilePanel.classList.toggle('shop-nav--open', isOpen);
        body.classList.toggle('shop-nav-open', isOpen);
    }

    function toggleMobileNav() {
        if (!mobilePanel) {
            return;
        }

        const isOpen = !mobilePanel.classList.contains('shop-nav--open');
        setMobileNavOpen(isOpen);
    }

    function closeMobileNavOnDesktop() {
        if (window.innerWidth > 768) {
            setMobileNavOpen(false);
        }
    }

    function updateCartBadge(count) {
        if (!cartBadge) {
            return;
        }

        const safeCount = Number.isFinite(Number(count)) ? Math.max(0, Number(count)) : 0;
        cartBadge.textContent = String(safeCount);
        cartBadge.classList.remove('shop-cart-bump');
        void cartBadge.offsetWidth;
        cartBadge.classList.add('shop-cart-bump');
        cartBadge.toggleAttribute('hidden', safeCount === 0);
    }

    function removeToast(toast) {
        if (!toast) {
            return;
        }

        toast.classList.add('is-leaving');
        window.setTimeout(function () {
            toast.remove();
        }, 220);
    }

    function showToast(message, type, duration) {
        if (!toastContainer || !message) {
            return;
        }

        const variant = type || 'info';
        const timeout = Number.isFinite(Number(duration)) ? Number(duration) : 2800;
        const iconMap = {
            success: 'bi-check-circle-fill',
            error: 'bi-exclamation-octagon-fill',
            warning: 'bi-exclamation-triangle-fill',
            info: 'bi-info-circle-fill'
        };

        const toast = document.createElement('div');
        toast.className = 'shop-toast shop-toast--' + variant;
        toast.innerHTML = '' +
            '<div class="shop-toast__icon"><i class="bi ' + (iconMap[variant] || iconMap.info) + '"></i></div>' +
            '<div class="shop-toast__content">' +
            '    <div class="shop-toast__message"></div>' +
            '</div>' +
            '<button type="button" class="shop-toast__close" aria-label="Đóng thông báo">' +
            '    <i class="bi bi-x-lg"></i>' +
            '</button>';

        toast.querySelector('.shop-toast__message').textContent = message;
        toast.querySelector('.shop-toast__close').addEventListener('click', function () {
            removeToast(toast);
        });

        toastContainer.appendChild(toast);

        window.setTimeout(function () {
            toast.classList.add('is-visible');
        }, 10);

        if (timeout > 0) {
            window.setTimeout(function () {
                removeToast(toast);
            }, timeout);
        }
    }

    function bindDismissibles() {
        document.querySelectorAll('[data-dismiss-parent]').forEach(function (button) {
            button.addEventListener('click', function () {
                const selector = button.getAttribute('data-dismiss-parent');
                const target = selector ? button.closest(selector) : button.parentElement;
                if (target) {
                    target.remove();
                }
            });
        });
    }

    function bindQuantityControls() {
        document.querySelectorAll('[data-quantity-control]').forEach(function (control) {
            const input = control.querySelector('[data-quantity-input]');
            const decrease = control.querySelector('[data-quantity-decrease]');
            const increase = control.querySelector('[data-quantity-increase]');

            if (!input) {
                return;
            }

            const min = Number(input.min || 1);
            const max = Number(input.max || Number.MAX_SAFE_INTEGER);

            function setValue(nextValue) {
                const clamped = Math.min(Math.max(nextValue, min), max);
                input.value = clamped;
                input.dispatchEvent(new Event('change', { bubbles: true }));
            }

            if (decrease) {
                decrease.addEventListener('click', function () {
                    setValue(Number(input.value || min) - 1);
                });
            }

            if (increase) {
                increase.addEventListener('click', function () {
                    setValue(Number(input.value || min) + 1);
                });
            }
        });
    }

    function bindAutoHideFlash() {
        document.querySelectorAll('[data-auto-hide]').forEach(function (element) {
            const delay = Number(element.getAttribute('data-auto-hide')) || 2800;
            window.setTimeout(function () {
                element.classList.add('is-leaving');
                window.setTimeout(function () {
                    element.remove();
                }, 220);
            }, delay);
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        toggleScrolledState();
        closeMobileNavOnDesktop();
        bindDismissibles();
        bindQuantityControls();
        bindAutoHideFlash();

        if (mobileToggle) {
            mobileToggle.addEventListener('click', toggleMobileNav);
        }

        window.addEventListener('scroll', toggleScrolledState, { passive: true });
        window.addEventListener('resize', closeMobileNavOnDesktop);
        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                setMobileNavOpen(false);
            }
        });
    });

    window.ShopLayout = {
        showToast: showToast,
        updateCartBadge: updateCartBadge,
        openMobileNav: function () { setMobileNavOpen(true); },
        closeMobileNav: function () { setMobileNavOpen(false); }
    };
})();
