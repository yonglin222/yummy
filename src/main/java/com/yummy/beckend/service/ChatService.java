package com.yummy.beckend.service;

import com.yummy.beckend.dto.ChatResponse;
import com.yummy.beckend.dto.RecipeDto;
import com.yummy.beckend.dto.AiRecommendResponse;
import com.yummy.beckend.dao.RecipeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ChatService {

    @Autowired
    private AiService aiService;   // FastAPI 호출

    @Autowired
    private RecipeDAO recipeDAO;

    public ChatResponse chat(String userMessage, String userId) {

        // 1️⃣ FastAPI 호출
        AiRecommendResponse aiResult =
                aiService.recommend(userMessage, userId);

        // ✅ Lombok getter 사용
        String answer = aiResult.getAnswer();

        // 2️⃣ 추천 레시피 조회
        RecipeDto recipe = null;
        if (aiResult.getRecipeId() != null) {
            try {
                recipe = recipeDAO.findById(
                        aiResult.getRecipeId(),
                        null   // 게스트
                );
            } catch (SQLException e) {
                recipe = null;
            }
        }

        // 3️⃣ 프론트 응답
        return new ChatResponse(answer, recipe);
    }
}
