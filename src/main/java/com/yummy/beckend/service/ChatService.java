package com.yummy.beckend.service;

import com.yummy.beckend.dto.ChatResponse;
import com.yummy.beckend.dto.RecipeDto;
import com.yummy.beckend.dto.AiRecommendResponse;
import com.yummy.beckend.dao.RecipeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private AiService aiService;   // FastAPI 호출

    @Autowired
    private RecipeDAO recipeDAO;

    @Autowired
    private FridgeService fridgeService;

    public ChatResponse chat(String userMessage, String userId) {

        // 1️⃣ FastAPI 호출
        AiRecommendResponse aiResult =
                aiService.recommendChat(userMessage, userId);

        // 🔎 디버그 로그 (문제 없으면 나중에 제거)
        System.out.println("AI RESULT answer = " + aiResult.getAnswer());
        System.out.println("AI RESULT recipeId = " + aiResult.getRecipeId());
        System.out.println("AI RESULT tags = " + aiResult.getTags());

        // 2️⃣ 응답 메시지
        String answer = aiResult.getAnswer();

        // 3️⃣ 추천 레시피 조회
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

        // 4️⃣ 프론트 응답
        return new ChatResponse(answer, recipe);
    }


    public ChatResponse recommendFromFridge(Long userId) throws SQLException {

        List<String> ingredients =
            fridgeService.getIngredientNamesForRecipeSearch(userId);

        AiRecommendResponse ai =
            aiService.recommendFridge(ingredients, String.valueOf(userId));

        RecipeDto recipe = null;
        if (ai.getRecipeId() != null) {
            recipe = recipeDAO.findById(ai.getRecipeId(), userId);
        }

        return new ChatResponse(ai.getAnswer(), recipe);
    }
}