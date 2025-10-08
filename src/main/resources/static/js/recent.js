(async function renderRecentIssues() {
  const container = document.querySelector('#recentGrid'); // we will add id to grid
  if (!container) return;

  // helper: status label + classes (matches CSS in your files)
  function badgeHtml(status) {
    const s = (status || 'SUBMITTED').toUpperCase();
    if (s === 'SUBMITTED') return '<span class="status-chip text-red-600 bg-white/80 border border-red-100">Reported</span>';
    if (s === 'IN_PROGRESS' || s === 'IN-PROGRESS') return '<span class="status-chip text-amber-600 bg-white/80 border border-amber-100">In Progress</span>';
    if (s === 'RESOLVED') return '<span class="status-chip text-emerald-600 bg-white/80 border border-emerald-100">Resolved</span>';
    if (s === 'REJECTED') return '<span class="status-chip text-gray-600 bg-white/80 border border-gray-100">Rejected</span>';
    return `<span class="status-chip bg-white/80">${s}</span>`;
  }

  function timeAgo(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    const diff = (Date.now() - d.getTime()) / 1000; // seconds
    if (diff < 60) return Math.floor(diff) + 's ago';
    if (diff < 3600) return Math.floor(diff/60) + 'm ago';
    if (diff < 86400) return Math.floor(diff/3600) + 'h ago';
    if (diff < 604800) return Math.floor(diff/86400) + 'd ago';
    return d.toLocaleDateString();
  }

  try {
    const res = await fetch('/api/complaints/recent?limit=3');
    if (!res.ok) throw new Error('Network response not OK');
    const list = await res.json();
    if (!Array.isArray(list) || list.length === 0) {
      container.innerHTML = '<div class="text-sm text-gray-500">No recent reports</div>';
      return;
    }

    // build cards
    container.innerHTML = list.map(item => {
      const imageUrl = item.imageUrl ? item.imageUrl : '/images/recent-placeholder.jpg';
      const location = item.address ? item.address : (item.latitude && item.longitude ? `(${item.latitude.toFixed(4)}, ${item.longitude.toFixed(4)})` : 'Location unknown');
      const label = badgeHtml(item.status);
      const when = timeAgo(item.createdAt);
      const trackLink = '/track.html?trackingId=' + encodeURIComponent(item.trackingId);

      return `
      <article class="relative bg-white rounded-lg overflow-hidden shadow-md" data-aos="fade-up">
        <a href="${trackLink}" class="block">
          <div class="h-44 bg-cover bg-center" style="background-image: url('${imageUrl}');"></div>
        </a>
        <div class="absolute top-3 left-3">${label}</div>
        <div class="p-4">
          <a href="${trackLink}" class="block">
            <h3 class="font-semibold">${escapeHtml(item.title || 'Untitled')}</h3>
          </a>
          <div class="text-sm text-gray-500 mt-1">${escapeHtml(location)} • <span class="text-xs">${when}</span></div>
        </div>
      </article>
      `;
    }).join('\n');

  } catch (err) {
    console.error('Failed to load recent issues', err);
    // keep existing placeholders in markup as fallback (do not overwrite)
  }

  // small helper to avoid XSS (we control API but be safe)
  function escapeHtml(s) {
    if (!s) return '';
    return String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');
  }
})();
