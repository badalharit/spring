<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .top { display:flex; align-items:center; justify-content:space-between; margin-bottom: 16px; }
        .pager button { margin-right: 8px; padding: 8px 12px; }
        .item { margin: 12px 0; border: 1px solid #eee; border-radius: 8px; padding: 10px; }
        pre { white-space: pre-wrap; word-wrap: break-word; }
    </style>
</head>
<body>
<div class="top">
    <h2>JSON Payload Dashboard</h2>
    <form method="post" action="<%= request.getContextPath() %>/logout" style="margin: 0;">
        <button type="submit" style="padding: 8px 12px;">Logout</button>
    </form>
</div>


<div class="pager">
    <button onclick="prevPage()">< Prev</button>
    <span id="pageInfo"></span>
    <button onclick="nextPage()">Next ></button>
</div>

<div id="items"></div>

<script>
    let currentPage = 0;
    const pageSize = 10;

    function esc(str) {
        return String(str)
            .replaceAll('&','&amp;')
            .replaceAll('<','<')
            .replaceAll('>','>')
            .replaceAll('"','"')
            .replaceAll("'",'&#039;');
    }

    function render(data) {
        const itemsEl = document.getElementById('items');
        itemsEl.innerHTML = '';

        const page = data.page ?? 0;
        const totalPages = data.totalPages ?? 0;

        document.getElementById('pageInfo').textContent = `Page ${page + 1} of ${totalPages}`;

        const items = data.items ?? [];
        for (const it of items) {
            // Basic expand/minimize using <details>
            const id = it._id ?? '';
            const json = JSON.stringify(it, null, 2);

            const div = document.createElement('div');
            div.className = 'item';
            div.innerHTML = `
                <details>
                    <summary>Payload ${id ? '(id=' + esc(id) + ')' : ''}</summary>
                    <pre>${esc(json)}</pre>
                </details>
            `;
            itemsEl.appendChild(div);
        }

        currentPage = page;
    }

    async function loadPage(page) {
        const res = await fetch(`${window.location.origin}${window.location.pathname}../../api/statements?page=${page}&size=${pageSize}`, { credentials: 'include' });
        const data = await res.json();
        render(data);
    }

    function prevPage() {
        if (currentPage <= 0) return;
        loadPage(currentPage - 1);
    }

    function nextPage() {
        // naive next
        loadPage(currentPage + 1);
    }

    // initial
    loadPage(0);
</script>
</body>
</html>

