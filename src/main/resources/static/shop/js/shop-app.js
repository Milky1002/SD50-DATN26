(function () {
    'use strict';

    function injectEnhancementStyles() {
        if (!document.head || document.getElementById('shopAppEnhancements')) {
            return;
        }

        var style = document.createElement('style');
        style.id = 'shopAppEnhancements';
        style.textContent = '' +
            '.shop-spin{display:inline-block;animation:shop-spin 1s linear infinite;}' +
            '@keyframes shop-spin{from{transform:rotate(0deg);}to{transform:rotate(360deg);}}' +
            '.shop-stagger--hidden > *{opacity:0;transform:translateY(24px);transition:opacity .45s ease,transform .45s ease;}' +
            '.shop-stagger--visible > *{opacity:1;transform:translateY(0);}' +
            '.shop-search__input.is-searching{background-image:linear-gradient(90deg, rgba(214, 43, 62, 0.08), rgba(214, 43, 62, 0));}';
        document.head.appendChild(style);
    }

    function getShopLayoutApi() {
        return window.ShopLayout || {};
    }

    function showToast(message, type) {
        var shopLayout = getShopLayoutApi();
        if (shopLayout.showToast) {
            shopLayout.showToast(message, type);
        }
    }

    function updateCartBadgeCount(count) {
        var shopLayout = getShopLayoutApi();
        if (shopLayout.updateCartBadge) {
            shopLayout.updateCartBadge(count);
        }
    }

    function readCartBadgeCount() {
        var badge = document.querySelector('[data-cart-badge]');
        var currentCount = badge ? parseInt(badge.textContent || '0', 10) : 0;
        return isNaN(currentCount) ? 0 : currentCount;
    }

    function readAddedQuantity(form) {
        var qtyInput = form.querySelector('[name="soLuong"]');
        var quantity = qtyInput ? parseInt(qtyInput.value || '1', 10) : 1;
        if (isNaN(quantity) || quantity < 1) {
            return 1;
        }
        return quantity;
    }

    function setSubmitButtonLoading(button, isLoading) {
        if (!button) {
            return;
        }

        if (isLoading) {
            if (!button.getAttribute('data-original-html')) {
                button.setAttribute('data-original-html', button.innerHTML);
            }
            button.disabled = true;
            button.innerHTML = '<i class="bi bi-arrow-repeat shop-spin"></i> Đang thêm...';
            return;
        }

        button.disabled = false;
        if (button.getAttribute('data-original-html')) {
            button.innerHTML = button.getAttribute('data-original-html');
            button.removeAttribute('data-original-html');
        }
    }

    function submitFormNormally(form) {
        form.setAttribute('data-shop-app-bypass', 'true');
        if (form.requestSubmit) {
            form.requestSubmit();
            return;
        }
        HTMLFormElement.prototype.submit.call(form);
    }

    function initAjaxAddToCart() {
        document.querySelectorAll('form[action*="/gio-hang/them"]').forEach(function (form) {
            form.addEventListener('submit', function (event) {
                var button;
                var formData;

                if (form.getAttribute('data-shop-app-bypass') === 'true') {
                    form.removeAttribute('data-shop-app-bypass');
                    return;
                }

                button = form.querySelector('button[type="submit"]');
                if (button && button.disabled) {
                    event.preventDefault();
                    return;
                }

                event.preventDefault();
                formData = new FormData(form);
                setSubmitButtonLoading(button, true);

                var ajaxUrl = form.action.replace('/gio-hang/them', '/gio-hang/them-ajax');

                fetch(ajaxUrl, {
                    method: 'POST',
                    body: formData,
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    credentials: 'same-origin'
                })
                    .then(function (response) {
                        if (!response.ok) {
                            throw new Error('ADD_TO_CART_FAILED');
                        }
                        return response.json();
                    })
                    .then(function (data) {
                        if (!data || !data.success) {
                            showToast(data && data.message ? data.message : 'Không thể thêm sản phẩm vào giỏ hàng.', 'error');
                            return;
                        }

                        updateCartBadgeCount(data.cartItemCount);
                        showToast(data.message || 'Đã thêm sản phẩm vào giỏ hàng!', 'success');
                    })
                    .catch(function () {
                        setSubmitButtonLoading(button, false);
                        submitFormNormally(form);
                    })
                    .finally(function () {
                        setSubmitButtonLoading(button, false);
                    });
            });
        });
    }

    function initSearchFeedback() {
        var searchInput = document.querySelector('.shop-search__input');
        var searchForm = searchInput ? searchInput.form : null;
        var debounceTimer;

        if (!searchInput || !searchForm) {
            return;
        }

        searchInput.addEventListener('input', function () {
            clearTimeout(debounceTimer);

            if ((searchInput.value || '').replace(/^\s+|\s+$/g, '').length < 2) {
                searchInput.classList.remove('is-searching');
                return;
            }

            searchInput.classList.add('is-searching');
            debounceTimer = window.setTimeout(function () {
                searchInput.classList.remove('is-searching');
            }, 300);
        });

        searchForm.addEventListener('submit', function () {
            searchInput.classList.remove('is-searching');
        });
    }

    function initScrollAnimations() {
        var observer;

        if (!('IntersectionObserver' in window)) {
            // Fallback: just make everything visible so nothing is permanently hidden
            document.querySelectorAll('.shop-stagger').forEach(function (element) {
                element.classList.add('shop-stagger--visible');
            });
            return;
        }

        observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (!entry.isIntersecting) {
                    return;
                }

                entry.target.classList.add('shop-stagger--visible');
                entry.target.classList.remove('shop-stagger--hidden');
                observer.unobserve(entry.target);
            });
        }, {
            threshold: 0.05,
            rootMargin: '0px 0px -20px 0px'
        });

        document.querySelectorAll('.shop-stagger').forEach(function (element) {
            var rect = element.getBoundingClientRect();
            // Use rect.top < window.innerHeight only — do NOT require rect.bottom > 0.
            // When DOMContentLoaded fires before fonts/images load, above-fold elements
            // may have rect.bottom === 0 (zero height). Excluding them wrongly adds
            // shop-stagger--hidden and makes the content invisible until a reload.
            if (rect.top < window.innerHeight) {
                element.classList.add('shop-stagger--visible');
            } else {
                element.classList.add('shop-stagger--hidden');
                observer.observe(element);
            }
        });
    }

    function initSmoothScroll() {
        document.querySelectorAll('a[href^="#"]').forEach(function (anchor) {
            anchor.addEventListener('click', function (event) {
                var targetId = anchor.getAttribute('href');
                var target;

                if (!targetId || targetId === '#') {
                    return;
                }

                target = document.querySelector(targetId);
                if (!target) {
                    return;
                }

                event.preventDefault();
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            });
        });
    }

    function initCartAutoUpdate() {
        document.querySelectorAll('.shop-cart__item').forEach(function (item) {
            var qtyInput = item.querySelector('[data-quantity-input]');
            var updateForm = item.querySelector('form[action*="/gio-hang/cap-nhat"]');
            var debounceTimer;

            if (!qtyInput || !updateForm) {
                return;
            }

            qtyInput.addEventListener('change', function () {
                clearTimeout(debounceTimer);
                debounceTimer = window.setTimeout(function () {
                    updateForm.submit();
                }, 500);
            });
        });
    }

    function initDragScroll() {
        document.querySelectorAll('[data-drag-scroll]').forEach(function (slider) {
            var isDown = false;
            var startX = 0;
            var scrollLeft = 0;

            slider.addEventListener('mousedown', function (event) {
                isDown = true;
                startX = event.pageX - slider.offsetLeft;
                scrollLeft = slider.scrollLeft;
            });

            slider.addEventListener('mouseleave', function () {
                isDown = false;
            });

            slider.addEventListener('mouseup', function () {
                isDown = false;
            });

            slider.addEventListener('mousemove', function (event) {
                var x;
                var walk;

                if (!isDown) {
                    return;
                }

                event.preventDefault();
                x = event.pageX - slider.offsetLeft;
                walk = (x - startX) * 1.2;
                slider.scrollLeft = scrollLeft - walk;
            });
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        injectEnhancementStyles();
        initAjaxAddToCart();
        initSearchFeedback();
        initScrollAnimations();
        initSmoothScroll();
        initCartAutoUpdate();
        initDragScroll();
    });

    // Re-run scroll animations on bfcache restore (browser back/forward navigation).
    // Without this, elements that got shop-stagger--hidden before the user navigated away
    // remain hidden when the browser restores the page from its cache.
    window.addEventListener('pageshow', function (event) {
        if (event.persisted) {
            document.querySelectorAll('.shop-stagger').forEach(function (element) {
                var rect = element.getBoundingClientRect();
                if (rect.top < window.innerHeight) {
                    element.classList.add('shop-stagger--visible');
                    element.classList.remove('shop-stagger--hidden');
                }
            });
        }
    });
})();
