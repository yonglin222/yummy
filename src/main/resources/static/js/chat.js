const chatBox = document.getElementById("chatBox");
const chatForm = document.getElementById("chatForm");
const userInput = document.getElementById("userInput");

// 1. 타이핑 인디케이터(...) 표시 함수
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

// 2. 타이핑 인디케이터 제거 함수
function hideTyping() {
    const typing = document.getElementById("typingIndicator");
    if (typing) typing.remove();
}

// 3. 메시지 추가 함수 (말풍선 생성)
function appendMessage(role, text) {
    const div = document.createElement("div");
    div.classList.add("msg", role);
    div.innerHTML = `<div class="bubble">${text}</div>`;
    chatBox.appendChild(div);
    chatBox.scrollTop = chatBox.scrollHeight;
}

// 4. 레시피 추천 카드 추가 함수 (선택 사항)
function appendRecipeCard(recipe) {
    const div = document.createElement("div");
    div.classList.add("msg", "bot");
    
    // 간단한 카드 형태의 HTML
    const cardHtml = `
        <div class="bubble" style="background: #fff; border: 1px solid #ddd; padding: 0; overflow: hidden;">
            <div style="padding: 15px;">
                <h3 style="margin: 0 0 10px 0; font-size: 16px;">🍳 ${recipe.name}</h3>
                <p style="margin: 0; font-size: 13px; color: #666;">
                    ${recipe.serving}인분 · ${recipe.time}분
                </p>
            </div>
            <a href="/recipe/detail/${recipe.recipeId}" 
               style="display: block; background: #6bae59; color: white; text-align: center; 
                      padding: 10px; text-decoration: none; font-size: 14px;">
               레시피 보러가기
            </a>
        </div>
    `;
    
    div.innerHTML = cardHtml;
    chatBox.appendChild(div);
    chatBox.scrollTop = chatBox.scrollHeight;
}

// 5. 전송 이벤트 핸들러
chatForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const text = userInput.value.trim();
    if (!text) return;

    // (1) 사용자 메시지 화면에 표시
    appendMessage("user", text);
    userInput.value = "";

    // (2) 로딩 표시
    showTyping();

    // (3) 실제 서버 통신 (AJAX)
    fetch("/chat/send", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ message: text }) // DTO 필드명 'message'와 일치
    })
    .then(res => {
        if (!res.ok) throw new Error("Network response was not ok");
        return res.json();
    })
    .then(data => {
        // 로딩 제거
        hideTyping();
        
        // AI 텍스트 응답 표시
        if (data.response) {
            appendMessage("bot", data.response);
        }

        // 추천 레시피가 함께 왔다면 카드 표시
        if (data.recommendedRecipe) {
            appendRecipeCard(data.recommendedRecipe);
        }
    })
    .catch(err => {
        hideTyping();
        console.error("Error:", err);
        appendMessage("bot", "죄송해요, 잠시 오류가 발생했어요. 다시 시도해주세요. 😥");
    });
});

