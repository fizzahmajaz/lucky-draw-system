/* ---- Common reusable JS ---- */

// API Wrapper
async function apiFetch(url, method, data) {
    var options = {
        method: method,
        headers: { "Content-Type": "application/json" }
    };

    if (method === "POST" || method === "PUT") {
        options.body = JSON.stringify(data);
    }

    try {
        var res = await fetch(url, options);
        var json = await res.json();
        return json;
    } catch (e) {
        console.error("Network error:", e);
        return { success: false, message: "Network error" };
    }
}

// Toast
function showToast(message, success) {
    var box = document.getElementById("toast-box");
    if (!box) {
        box = document.createElement("div");
        box.id = "toast-box";
        box.className = "toast-box";
        document.body.appendChild(box);
    }

    var div = document.createElement("div");
    div.className = "toast-card";
    div.innerHTML =
        "<strong>" +
        (success ? "Success" : "Error") +
        "</strong><div style='margin-top:6px;'>" +
        message +
        "</div>";

    box.appendChild(div);

    setTimeout(function() {
        div.style.opacity = "0";
        div.style.transition = "0.4s";
        setTimeout(function() {
            div.remove();
        }, 450);
    }, 2500);
}

// Redirect helper
function redirectTo(url) {
    window.location.href = url;
}