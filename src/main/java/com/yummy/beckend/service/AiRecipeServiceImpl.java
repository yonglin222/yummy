// package com.yummy.beckend.service;

// import com.yummy.beckend.dao.RecipeDAO;
// import com.yummy.beckend.dto.RecipeDto;
// import com.yummy.beckend.dto.AiSearchRequest;
// import com.yummy.beckend.exception.ExternalApiException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// @Service
// public class AiRecipeServiceImpl implements AiRecipeService {

//     @Autowired
//     private RestTemplate restTemplate;

//     @Autowired
//     private RecipeDAO recipeDAO; // AI가 반환한 ID로 레시피 상세를 조회하기 위함

//     // application.properties에 설정이 필요합니다.
//     @Value("${fastapi.ai.url:/ai}") // 기본값은 /ai로 설정
//     private String FASTAPI_BASE_URL;

//     // Mock 레시피 ID (DB에 더미 데이터 삽입 후 사용할 예정)
//     private static final List<Long> MOCK_RECIPE_IDS = List.of(1L, 2L, 3L); 

//     /**
//      * A-1. 자연어 기반 AI 레시피 검색 (FastAPI 연동 로직)
//      */
//     @Override
//     public List<RecipeDto> searchByNaturalLanguage(String naturalQuery, Long userId) throws ExternalApiException, SQLException {
//         // LLM이 확정되면 사용할 실제 로직:
//         /*
//         try {
//             String url = FASTAPI_BASE_URL + "/search/query";
//             AiSearchRequest request = new AiSearchRequest();
//             request.setQuery(naturalQuery);
//             request.setUserId(userId);
            
//             // FastAPI는 List<Long> 형태의 추천 레시피 ID 목록을 반환한다고 가정
//             ResponseEntity<List<Long>> response = restTemplate.exchange(
//                 url, 
//                 HttpMethod.POST, 
//                 // ... HttpEntity (요청 본문) 설정,
//                 new ParameterizedTypeReference<List<Long>>() {}
//             );
            
//             List<Long> recommendedIds = response.getBody();
//             // DB에서 ID 목록에 해당하는 RecipeDto 목록 조회 및 반환 (RecipeDAO에 메서드 추가 필요)
//             // return recipeDAO.findByIds(recommendedIds);

//         } catch (Exception e) {
//             throw new ExternalApiException("FastAPI 서버 통신 오류", e);
//         }
//         */

//         // ⭐️ LLM 미정 시 Mock 데이터 반환 (선행 개발)
//         System.out.println("DEBUG: AI 검색 요청 수신 - " + naturalQuery + ". Mock 데이터를 반환합니다.");
//         return getMockRecipes();
//     }

//     /**
//      * B-1. 보유 재료 활용 레시피 검색 (FastAPI 연동 로직)
//      */
//     @Override
//     public List<RecipeDto> searchByIngredients(List<String> ingredientNames, Long userId) throws ExternalApiException, SQLException {
//         // LLM이 확정되면 사용할 실제 로직:
//         /*
//         try {
//             String url = FASTAPI_BASEAPI_URL + "/search/ingredients";
//             // ... (FastAPI 호출 및 응답 처리 로직)
//             // ... (RecipeDAO를 통한 최종 RecipeDto 목록 조회)
//         } catch (Exception e) {
//             throw new ExternalApiException("FastAPI 서버 통신 오류", e);
//         }
//         */

//         // ⭐️ LLM 미정 시 Mock 데이터 반환 (선행 개발)
//         System.out.println("DEBUG: 재료 활용 요청 수신 - " + ingredientNames + ". Mock 데이터를 반환합니다.");
//         return getMockRecipes();
//     }

//     /**
//      * Mock 데이터를 실제 DB에서 조회하여 반환하는 임시 메서드
//      */
//     @Override
//     public List<RecipeDto> getMockRecipes() {
//         try {
//             // Mock ID를 사용하여 DB에서 레시피를 조회합니다.
//             return MOCK_RECIPE_IDS.stream()
//                     .map(id -> {
//                         try {
//                             return recipeDAO.findById(id);
//                         } catch (SQLException e) {
//                             System.err.println("Mock Recipe ID 조회 중 DB 오류: " + e.getMessage());
//                             return null;
//                         }
//                     })
//                     .filter(r -> r != null)
//                     .collect(Collectors.toList());
//         } catch (Exception e) {
//             // DB 연결 문제 발생 시 빈 리스트 반환
//             return new ArrayList<>();
//         }
//     }
// }