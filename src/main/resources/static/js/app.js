// TalentTrack Lite — Client-side JS
document.addEventListener('DOMContentLoaded', function () {
    // Highlight active sidebar link based on current path
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-nav-item').forEach(function (link) {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        } else if (href === '/' + currentPath.split('/')[1]) {
            link.classList.add('active');
        }
    });
});
