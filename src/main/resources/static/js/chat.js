const chatBox = document.getElementById("chatBox");
const chatForm = document.getElementById("chatForm");
const userInput = document.getElementById("userInput");

// ===============================================
// 1. 초기화 (메인화면에서 넘어온 데이터 처리)
// ===============================================
document.addEventListener("DOMContentLoaded", () => {
    /**
     * 컨트롤러에서 model.addAttribute("startMsg", msg)로 보낸 값을 
     * HTML 내 <script th:inline="javascript"> 세션에서 정의한 변수로 읽습니다.
     * 만약 HTML에 해당 스크립트가 없다면 URL 파라미터에서 직접 추출합니다.
     */
    const urlParams = new URLSearchParams(window.location.search);
    const initialMsg = urlParams.get('msg');

    if (initialMsg) {
        // 1. 사용자 질문을 채팅창에 즉시 표시
        appendMessage("user", initialMsg);
        
        // 2. 서버(AI)에 비동기 답변 요청 시작
        sendToAi(initialMsg);
        
        // 3. URL 파라미터 깔끔하게 정리 (선택 사항: 새로고침 시 중복 요청 방지)
        window.history.replaceState({}, document.title, window.location.pathname);
    }
});

// ===============================================
// 2. 메시지 전송 로직
// ===============================================

// 공통 전송 함수 (AJAX 호출)
function sendToAi(text) {
    if (!text) return;

    // 로딩 인디케이터 표시
    showTyping();

    // 서버 전송 (AJAX)
    fetch("/chat/send", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ message: text }) 
    })
    .then(res => {
        if (!res.ok) throw new Error("서버 응답 오류");
        return res.json();
    })
    .then(data => {
        // 로딩 제거
        hideTyping();

        // 1. AI 텍스트 응답 표시
        if (data.response) {
            appendMessage("bot", data.response);
        }

        // 2. 레시피가 함께 왔다면 카드 표시
        if (data.recommendedRecipe) {
            appendRecipeCard(data.recommendedRecipe);
        }
    })
    .catch(err => {
        console.error("Chat Error:", err);
        hideTyping();
        appendMessage("bot", "죄송해요, AI 야미와 연결이 원활하지 않아요 😥");
    });
}

// 채팅창 내부 폼 전송 이벤트
chatForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const text = userInput.value.trim();
    if (!text) return;

    // 사용자 메시지 화면 표시
    appendMessage("user", text);
    userInput.value = "";
    userInput.focus();

    // AI에게 요청
    sendToAi(text);
});


// ===============================================
// 3. 화면 렌더링 헬퍼 함수들
// ===============================================

// 메시지 말풍선 추가
function appendMessage(role, text) {
    const div = document.createElement("div");
    div.classList.add("msg", role);
    
    // 줄바꿈 처리
    const formattedText = text.replace(/\n/g, "<br>");
    
    div.innerHTML = `<div class="bubble">${formattedText}</div>`;
    chatBox.appendChild(div);
    scrollToBottom();
}

// 레시피 카드 추가
function appendRecipeCard(recipe) {
    const div = document.createElement("div");
    div.classList.add("msg", "bot");

    const cardHtml = `
        <div class="bubble recipe-bubble" style="background: #fff; border: 2px solid #6bbd45; padding: 0; overflow: hidden; width: 280px; text-align: left; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">
            <div style="padding: 15px;">
                <h3 style="margin: 0 0 10px; color: #222; font-size: 18px;">🍽 ${recipe.name || "추천 레시피"}</h3>
                
                <div style="font-size: 13px; color: #666; margin-bottom: 8px;">
                    <span>⏱ ${recipe.time ? recipe.time + "분" : "-"}</span> | 
                    <span>👤 ${recipe.serving ? recipe.serving + "인분" : "-"}</span>
                </div>
                
                <p style="font-size: 14px; color: #444; margin-bottom: 15px; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                    <strong>재료:</strong> ${recipe.ingredient || "상세보기 참조"}
                </p>

                <a href="/recipe/list?targetId=${recipe.recipeId}" class="recipe-link-btn" 
                   style="display: block; text-align: center; background: #6bbd45; color: white; padding: 10px; text-decoration: none; border-radius: 8px; font-weight: bold; transition: background 0.3s;">
                   레시피 보러가기 →
                </a>
            </div>
        </div>
    `;

    div.innerHTML = cardHtml;
    chatBox.appendChild(div);
    scrollToBottom();
}

// 로딩 인디케이터 (...) 표시
function showTyping() {
    // 중복 생성 방지
    if (document.getElementById("typingIndicator")) return;

    const typing = document.createElement("div");
    typing.classList.add("msg", "bot");
    typing.id = "typingIndicator";
    typing.innerHTML = `
        <div class="typing-indicator">
            <span>.</span><span>.</span><span>.</span>
        </div>
    `;
    chatBox.appendChild(typing);
    scrollToBottom();
}

function hideTyping() {
    const typing = document.getElementById("typingIndicator");
    if (typing) typing.remove();
}

// 스크롤 제어
function scrollToBottom() {
    chatBox.scrollTop = chatBox.scrollHeight;
}