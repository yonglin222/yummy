// package com.yummy.beckend.controller;

// import jakarta.servlet.http.HttpSession;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;

// import com.yummy.beckend.dto.RecipeDto;
// import com.yummy.beckend.dto.UserDto;
// import com.yummy.beckend.exception.ExternalApiException;
// import com.yummy.beckend.exception.UnauthorizedAccessException;
// import com.yummy.beckend.service.AiRecipeService;
// import com.yummy.beckend.service.FridgeService; // B-1 기능을 위해 냉장고 서비스 주입

// import java.sql.SQLException;
// import java.util.List;
// import java.util.Map;
// import java.util.HashMap;

// @Controller
// @RequestMapping("/ai")
// public class AiRecipeController {

//     @Autowired
//     private AiRecipeService aiRecipeService;

//     @Autowired
//     private FridgeService fridgeService;

//     // --- 유틸리티 메서드: 세션에서 사용자 ID를 가져옵니다. (로그인 안되어 있으면 null) ---
//     private Long getUserId(HttpSession session) {
//         UserDto user = (UserDto) session.getAttribute("loginUser");
//         return (user != null) ? user.getId() : null;
//     }

//     /**
//      * A-1. 자연어 기반 AI 레시피 검색 AJAX 엔드포인트
//      */
//     @GetMapping("/searchRecipe")
//     @ResponseBody
//     public Map<String, Object> searchByQuery(@RequestParam("query") String query, HttpSession session) {
//         Map<String, Object> response = new HashMap<>();
//         Long userId = getUserId(session);

//         if (userId == null) {
//             response.put("status", "UNAUTHORIZED");
//             response.put("message", "로그인 후 이용 가능합니다.");
//             return response;
//         }

//         try {
//             // 1. AI Service를 통해 레시피 ID 목록을 받습니다 (Mock 또는 실제 통신)
//             List<RecipeDto> recommendedRecipes = aiRecipeService.searchByNaturalLanguage(query, userId);

//             response.put("status", "OK");
//             response.put("recipes", recommendedRecipes);
//             response.put("message", "AI 검색 결과를 가져왔습니다.");

//         } catch (ExternalApiException e) {
//             response.put("status", "API_ERROR");
//             response.put("message", "AI 서버와 통신 중 오류가 발생했습니다.");
//             System.err.println("FastAPI 통신 오류: " + e.getMessage());
//         } catch (SQLException e) {
//             response.put("status", "DB_ERROR");
//             response.put("message", "데이터베이스 오류가 발생했습니다.");
//             e.printStackTrace();
//         }

//         return response;
//     }

//     /**
//      * B-1. 보유 재료 활용 레시피 검색 AJAX 엔드포인트
//      */
//     @GetMapping("/searchByIngredients")
//     @ResponseBody
//     public Map<String, Object> searchByIngredients(HttpSession session) {
//         Map<String, Object> response = new HashMap<>();
//         Long userId = getUserId(session);

//         if (userId == null) {
//             response.put("status", "UNAUTHORIZED");
//             response.put("message", "로그인 후 이용 가능합니다.");
//             return response;
//         }

//         try {
//             // 1. Fridge Service에서 사용자의 보유 재료 목록을 조회
//             List<String> ingredientNames = fridgeService.getIngredientNamesForRecipeSearch(userId);

//             if (ingredientNames.isEmpty()) {
//                 response.put("status", "NO_INGREDIENTS");
//                 response.put("message", "냉장고에 등록된 재료가 없습니다.");
//                 return response;
//             }

//             // 2. AI Service로 재료 목록을 전달하여 추천 레시피 목록을 받음
//             List<RecipeDto> recommendedRecipes = aiRecipeService.searchByIngredients(ingredientNames, userId);

//             response.put("status", "OK");
//             response.put("recipes", recommendedRecipes);
//             response.put("message", "보유 재료를 활용한 레시피 추천 결과를 가져왔습니다.");

//         } catch (ExternalApiException e) {
//             response.put("status", "API_ERROR");
//             response.put("message", "AI 서버와 통신 중 오류가 발생했습니다.");
//         } catch (SQLException e) {
//             response.put("status", "DB_ERROR");
//             response.put("message", "데이터베이스 오류가 발생했습니다.");
//             e.printStackTrace();
//         }

//         return response;
//     }
// }