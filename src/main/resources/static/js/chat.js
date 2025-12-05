const chatBox = document.getElementById("chatBox");
const chatForm = document.getElementById("chatForm");
const userInput = document.getElementById("userInput");

// typing indicator 추가
function showTyping() {
    const typing = document.createElement("div");
    typing.classList.add("msg", "bot");
    typing.id = "typingIndicator";

    typing.innerHTML = `
        <div class="typing-indicator">
            <span>.</span><span>.</span><span>.</span>
        </div>
    `;

    chatBox.appendChild(typing);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function hideTyping() {
    const typing = document.getElementById("typingIndicator");
    if (typing) typing.remove();
}

// 메시지 추가 함수
function appendMessage(role, text) {
    const div = document.createElement("div");
    div.classList.add("msg", role);
    div.innerHTML = `<div class="bubble">${text}</div>`;
    chatBox.appendChild(div);
    chatBox.scrollTop = chatBox.scrollHeight;
}

// 전송 이벤트
chatForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const text = userInput.value.trim();
    if (!text) return;

    // 사용자 메시지
    appendMessage("user", text);
    userInput.value = "";

    // AI 타이핑(...) 표시
    showTyping();

    // 2.2초 후 AI 답변
    setTimeout(() => {
        hideTyping();
        appendMessage("bot", "레시피를 찾고 있어요… 🔎");
    }, 2200);

    // ★ 나중에 fetch 로직 넣을 자리
    // fetch("/api/ai", ...)
});
