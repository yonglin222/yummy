// 전역/스코프 변수를 선언하여 AI 쿼리 정보를 저장합니다.
let currentRecipeQuery = "";

document.addEventListener("DOMContentLoaded", () => {
    // URL 쿼리 파라미터에서 초기 정보를 가져옵니다.
    const urlParams = new URLSearchParams(window.location.search);
    const recipeId = urlParams.get('recipeId');
    const aiMessage = urlParams.get('aiMessage');
    
    // ⭐️ AI에게 보냈던 원본 쿼리를 저장합니다. (URL 디코딩 필요)
    const encodedRecipeQuery = urlParams.get('recipeQuery');
    if (encodedRecipeQuery) {
        currentRecipeQuery = decodeURIComponent(encodedRecipeQuery);
    }
    // console.log("저장된 쿼리:", currentRecipeQuery); // 디버깅용

    // 1. 초기 레시피 로드
    if (recipeId) {
        loadRecipe(recipeId, aiMessage);
    } else {
        document.getElementById("recipe-title").textContent = "레시피를 찾을 수 없습니다.";
        document.getElementById("ai-response-message").textContent = "AI 추천 과정에서 오류가 발생했거나 유효한 레시피 ID를 받지 못했습니다.";
    }


    // 2. 버튼 이벤트 설정
    document.getElementById("newRecipeBtn").addEventListener("click", () => {
        // ⭐️ 저장된 쿼리 텍스트를 사용하여 재요청
        reloadRecipe(currentRecipeQuery); 
    });

    document.getElementById("backToFridgeBtn").addEventListener("click", () => {
        // 나의 냉장고 목록 페이지로 돌아가기
        window.location.href = "/fridge/list";
    });
});

/**
 * [목표] 주어진 레시피 ID를 기반으로 상세 정보를 AJAX로 가져와 페이지를 채웁니다.
 */
function loadRecipe(recipeId, aiMessage) {
    // 💡 URL에서 가져온 메시지는 디코딩하여 사용
    document.getElementById("ai-response-message").textContent = decodeURIComponent(aiMessage) || "AI가 맞춤 레시피를 추천했습니다!";
    
    // 로딩 시작 (필요하다면)
    // showLoading(); 

    fetch(`/recipe/api/detail/${recipeId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            // hideLoading(); // 로딩 종료

            if (data.status === 'OK' && data.recipe) {
                const recipe = data.recipe;
                
                // 1. 제목 및 메타 정보
                document.getElementById("recipe-title").textContent = recipe.name;
                document.getElementById("recipe-cookTime").textContent = `${recipe.time}분`;
                document.getElementById("recipe-serving").textContent = `${recipe.serving}인분`;

                // 2. 재료 (ingredients)
                const ingList = document.getElementById("ingredient-list");
                ingList.innerHTML = recipe.ingredient.split(/[\n,]/)
                                                    .map(i => i.trim())
                                                    .filter(i => i.length > 0)
                                                    .map(i => `<li>${i}</li>`)
                                                    .join("");
                
                // 3. 양념 (seasoning)
                const seasoning = document.getElementById("seasoning-list");
                seasoning.innerHTML = recipe.spicyIngredient.split(/[\n,]/)
                                                            .map(s => s.trim())
                                                            .filter(s => s.length > 0)
                                                            .map(s => `<li>${s}</li>`)
                                                            .join("");

                // 4. 만드는 법 (methodSteps - Service에서 이미 리스트로 처리됨)
                const steps = document.getElementById("steps-list");
                steps.innerHTML = recipe.methodSteps.map(s => `<li>${s}</li>`).join("");

            } else {
                document.getElementById("recipe-title").textContent = "레시피 상세 정보를 불러오지 못했습니다.";
            }
        })
        .catch(error => {
            // hideLoading(); // 로딩 종료
            console.error('레시피 상세 정보 로드 오류:', error);
            document.getElementById("recipe-title").textContent = "데이터 통신 오류가 발생했습니다.";
        });
}


/**
 * ⭐️ '같은 재료로 다른 레시피 추천 받기' 로직 (수정됨)
 * 저장된 원본 AI 쿼리 텍스트를 서버에 다시 전송하고, 현재 창의 URL을 업데이트합니다.
 */
function reloadRecipe(recipeQueryText) {
    if (!recipeQueryText || recipeQueryText.trim() === "") {
        alert("이전 재료 정보가 없어 다시 추천을 요청할 수 없습니다. 냉장고 목록으로 돌아가 재시도해주세요.");
        window.location.href = "/fridge/list";
        return;
    }
    
    // 로딩 시작 (필요하다면 showLoading() 호출)
    console.log("AI 재추천 요청 시작...");

    // 1. 서버의 /fridge/recommend API를 재호출 (queryText 파라미터 사용)
    fetch('/fridge/recommend', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        // ⭐️⭐️⭐️ 파라미터 이름을 "recipeQuery"로 수정 ⭐️⭐️⭐️
        body: `recipeQuery=${encodeURIComponent(recipeQueryText)}` 
    })
    .then(response => response.json())
    .then(data => {
        // hideLoading(); // 로딩 숨김

        if (data.status === 'OK' && data.recipeId) {
            // 2. 성공적으로 새 레시피 ID를 받은 경우, 현재 창의 URL을 새 정보로 교체 (창 이동)
            const recipeId = data.recipeId;
            const aiMessage = encodeURIComponent(data.aiMessage);
            const newRecipeQuery = encodeURIComponent(data.recipeQuery); // AI에게 전송한 최종 쿼리 재저장

            const detailUrl = `/fridge/recipe-detail?recipeId=${recipeId}&aiMessage=${aiMessage}&recipeQuery=${newRecipeQuery}`;
            
            // 현재 창을 새로운 레시피 상세 페이지로 이동
            window.location.href = detailUrl;

        } else if (data.status === 'UNAUTHORIZED') {
            alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
            window.location.href = "/user/loginForm";
        } else if (data.status === 'OK' && !data.recipeId) {
            alert("AI가 새로운 레시피를 찾지 못했습니다. AI 답변: " + data.aiMessage);
        } else {
            alert("재추천 중 오류가 발생했습니다: " + (data.message || "서버 오류"));
        }
    })
    .catch(error => {
        // hideLoading();
        console.error('AI 재추천 API 호출 오류:', error);
        alert('서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
    });
}