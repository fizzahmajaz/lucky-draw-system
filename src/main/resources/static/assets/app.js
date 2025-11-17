const BASE_URL = "http://localhost:8080";

async function apiPost(url, data) {
    try {
        const res = await fetch(BASE_URL + url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        return await res.json();
    } catch (err) {
        console.error(err);
        alert("Network error");
    }
}

async function apiGet(url) {
    try {
        const res = await fetch(BASE_URL + url);
        return await res.json();
    } catch (err) {
        console.error(err);
        alert("Network error");
    }
}

// Toast
function showToast(msg, type = "success") {
    const bg = type === "success" ? "#6a00ff" : "#aa0000";
    const toast = document.createElement("div");
    toast.classList.add("glass-card");
    toast.style.position = "fixed";
    toast.style.top = "20px";
    toast.style.right = "20px";
    toast.style.background = bg;
    toast.style.padding = "12px 18px";
    toast.style.zIndex = "9999";
    toast.innerHTML = msg;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 2500);
}