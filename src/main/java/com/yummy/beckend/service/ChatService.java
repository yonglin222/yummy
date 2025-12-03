package com.yummy.beckend.service;

import com.yummy.beckend.dto.RecipeDto;
import com.yummy.beckend.dao.RecipeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ChatService {

    @Autowired
    private RecipeDAO recipeDAO;

    public String getAiResponse(String userMessage) {
        // ⭐️ 나중에 여기에 FastAPI 호출 로직이 들어갑니다.
        // 지금은 간단한 규칙 기반 로직(Mock)으로 동작합니다.

        if (userMessage.contains("안녕")) {
            return "안녕하세요! 저는 요리 추천 AI 야미입니다. 무엇을 도와드릴까요?";
        } else if (userMessage.contains("추천")) {
            return "오늘 같은 날씨에는 뜨끈한 국물 요리가 어떠세요? '레시피 추천해줘'라고 말씀하시면 구체적인 요리를 보여드릴게요.";
        } else {
            return "죄송해요, 아직 배우는 중이라 어려운 말은 잘 몰라요. '추천'이나 '안녕'이라고 말해보세요!";
        }
    }

    // 추천 레시피가 필요한 경우 DB에서 가져오는 로직 (Mock)
    public RecipeDto getRecommendedRecipe(String userMessage) {
        if (userMessage.contains("레시피")) {
            try {
                // 테스트용으로 무조건 ID 1번(간장 닭조림)을 추천한다고 가정
                return recipeDAO.findById(1L, null); 
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }
}