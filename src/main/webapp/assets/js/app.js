// Xac nhan truoc khi xoa: <form data-confirm="...">
document.addEventListener('submit', function (e) {
    var msg = e.target.getAttribute && e.target.getAttribute('data-confirm');
    if (msg && !window.confirm(msg)) {
        e.preventDefault();
    }
});

// Xem truoc anh bia tren form them/sua sach
(function () {
    var input = document.getElementById('coverImage');
    var box = document.getElementById('coverPreview');
    if (!input || !box) return;
    function render() {
        var v = input.value.trim();
        box.innerHTML = '';
        if (!v) return;
        var img = document.createElement('img');
        img.className = 'cover';
        img.alt = 'preview';
        img.src = /^https?:\/\//i.test(v) ? v : box.getAttribute('data-base') + encodeURIComponent(v);
        img.onerror = function () { box.textContent = 'Không tải được ảnh bìa này.'; };
        box.appendChild(img);
    }
    input.addEventListener('input', render);
    render();
})();

// Nut +/- so luong trong gio hang: doi gia tri trong [min, max] roi gui form cap nhat
document.addEventListener('click', function (e) {
    var btn = e.target.closest ? e.target.closest('.qty-btn') : null;
    if (!btn) return;
    var form = btn.closest('form');
    var input = form && form.querySelector('input[name=quantity]');
    if (!input) return;
    var min = parseInt(input.min || '1', 10), max = parseInt(input.max || '999', 10);
    var cur = parseInt(input.value, 10); if (isNaN(cur)) cur = min;
    var next = Math.max(min, Math.min(max, cur + parseInt(btn.getAttribute('data-delta'), 10)));
    if (next !== cur || cur > max) { input.value = next; form.submit(); }
});

// Chong bam dat hang 2 lan: <form data-once="1">
document.addEventListener('submit', function (e) {
    var f = e.target;
    if (!f.getAttribute || !f.getAttribute('data-once') || e.defaultPrevented) return;
    setTimeout(function () {
        var b = f.querySelector('button[type=submit]');
        if (b) { b.disabled = true; b.textContent = 'Đang xử lý...'; }
    }, 0);
});
