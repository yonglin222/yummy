// package com.yummy.beckend.controller;

// import com.yummy.beckend.dto.AiRecommendResponse;
// import com.yummy.beckend.service.AiService;
// import com.yummy.beckend.dto.RecipeDto;
// import com.yummy.beckend.service.RecipeService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// @Controller
// @RequiredArgsConstructor
// public class AiRecommendController {

//     private final AiService aiService;
//     private final RecipeService recipeService;

//     @GetMapping("/ai/recommend")
//     public String recommend(
//             @RequestParam String query,
//             @RequestParam(required = false) Long userId,
//             Model model
//     ) throws Exception {

//         // 🔹 회원이면 userId, 아니면 guest
//         String aiUserId = (userId != null)
//                 ? userId.toString()
//                 : "guest";

//         // 1️⃣ AI 추천 요청
//         AiRecommendResponse aiResult =
//                 aiService.recommend(query, aiUserId);

//         // 2️⃣ recipe_id → 기존 DB 로직 재사용
//         RecipeDto recipe = null;
//         if (aiResult.getRecipeId() != null) {
//             recipe = recipeService.getRecipeDetail(
//                     aiResult.getRecipeId(),
//                     userId
//             );
//         }

//         // 3️⃣ 화면 전달
//         model.addAttribute("query", aiResult.getQuery());
//         model.addAttribute("answer", aiResult.getAnswer());
//         model.addAttribute("recipe", recipe);

//         return "ai/ai-result";
//     }


// }
