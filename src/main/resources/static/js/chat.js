const chatBox = document.getElementById("chatBox");
const chatForm = document.getElementById("chatForm");
const userInput = document.getElementById("userInput");

// ===============================================
// 1. 초기화 (메인화면에서 넘어온 데이터 처리)
// ===============================================
document.addEventListener("DOMContentLoaded", () => {
    // room.html 내 <script>에서 정의한 변수 사용
    if (typeof startUserMsg !== 'undefined' && startUserMsg) {
        // 1. 사용자 질문 출력
        appendMessage("user", startUserMsg);
        
        // 2. AI 응답 출력
        if (typeof startAiMsg !== 'undefined' && startAiMsg) {
            appendMessage("bot", startAiMsg);
        }
        
        // 3. 추천 레시피가 있다면 카드 출력
        if (typeof startRecipe !== 'undefined' && startRecipe) {
            appendRecipeCard(startRecipe);
        }
    }
});

// ===============================================
// 2. 메시지 전송 로직
// ===============================================
chatForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const text = userInput.value.trim();
    if (!text) return;

    // 1. 사용자 메시지 화면 표시
    appendMessage("user", text);
    userInput.value = "";
    userInput.focus();

    // 2. 로딩 표시
    showTyping();

    // 3. 서버 전송 (AJAX)
    // DTO: ChatRequest { message: String }
    fetch("/chat/send", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ message: text }) 
    })
    .then(res => res.json())
    .then(data => {
        // DTO: ChatResponse { response: String, recommendedRecipe: RecipeDto }
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
        appendMessage("bot", "죄송해요, 오류가 발생해서 답변을 드릴 수 없어요 😥");
    });
});


// ===============================================
// 3. 화면 렌더링 헬퍼 함수들
// ===============================================

// 텍스트 메시지 추가
function appendMessage(role, text) {
    const div = document.createElement("div");
    div.classList.add("msg", role);
    
    // 줄바꿈 처리 (\n -> <br>)
    const formattedText = text.replace(/\n/g, "<br>");
    
    div.innerHTML = `<div class="bubble">${formattedText}</div>`;
    chatBox.appendChild(div);
    scrollToBottom();
}

// 레시피 카드 추가 (채팅방 내부에 표시)
function appendRecipeCard(recipe) {
    const div = document.createElement("div");
    div.classList.add("msg", "bot"); // 봇이 보낸 것처럼 표시

    // 레시피 카드 HTML 구조
    // RecipeDto 필드명: recipeId, name, ingredient(String), time(Integer), serving(Integer)
    const cardHtml = `
        <div class="bubble recipe-bubble" style="background: #fff; border: 2px solid #6bbd45; padding: 0; overflow: hidden; width: 280px; text-align: left;">
            <div style="padding: 15px;">
                <h3 style="margin: 0 0 10px; color: #222; font-size: 18px;">🍽 ${recipe.name || "추천 레시피"}</h3>
                
                <div style="font-size: 13px; color: #666; margin-bottom: 8px;">
                    <span>⏱ ${recipe.time ? recipe.time + "분" : "-"}</span> | 
                    <span>👤 ${recipe.serving ? recipe.serving + "인분" : "-"}</span>
                </div>
                
                <p style="font-size: 14px; color: #444; margin-bottom: 15px; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                    <strong>재료:</strong> ${recipe.ingredient || "상세보기 참조"}
                </p>

                <a href="/recipe/detail/${recipe.recipeId}" class="recipe-link-btn" 
                   style="display: block; text-align: center; background: #6bbd45; color: white; padding: 10px; text-decoration: none; border-radius: 8px; font-weight: bold;">
                   레시피 보러가기 →
                </a>
            </div>
        </div>
    `;

    div.innerHTML = cardHtml;
    chatBox.appendChild(div);
    scrollToBottom();
}

// 로딩 인디케이터 표시
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
    scrollToBottom();
}

function hideTyping() {
    const typing = document.getElementById("typingIndicator");
    if (typing) typing.remove();
}

// 스크롤 맨 아래로
function scrollToBottom() {
    chatBox.scrollTop = chatBox.scrollHeight;
}