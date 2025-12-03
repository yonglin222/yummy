// package com.yummy.beckend.service;

// import com.yummy.beckend.dto.RecipeDto;
// import com.yummy.beckend.dto.AiSearchRequest; // 요청 DTO는 새로 정의 필요
// import com.yummy.beckend.exception.ExternalApiException;

// import java.util.List;
// import java.util.Map;
// import java.sql.SQLException;

// public interface AiRecipeService {

//     // A-1. 자연어 기반 AI 레시피 검색
//     // LLM이 해석한 레시피 ID 목록(List<Long>)을 반환한다고 가정합니다.
//     List<RecipeDto> searchByNaturalLanguage(String naturalQuery, Long userId) throws ExternalApiException, SQLException;

//     // B-1. 보유 재료 활용 레시피 검색
//     // 사용자의 보유 재료명 리스트를 FastAPI로 전달합니다.
//     List<RecipeDto> searchByIngredients(List<String> ingredientNames, Long userId) throws ExternalApiException, SQLException;
    
//     // AI 연동 테스트를 위한 Mock 메서드 (임시)
//     List<RecipeDto> getMockRecipes();
// }