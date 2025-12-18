package com.yummy.beckend.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.yummy.beckend.dto.ChatResponse;
import com.yummy.beckend.dto.FridgeDto;
import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.exception.UnauthorizedAccessException;
import com.yummy.beckend.service.ChatService;
import com.yummy.beckend.service.FridgeService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "2. 냉장고 관리", description = "재료 등록, 수정, 삭제, 조회, AI 추천")
@Controller
@RequestMapping("/fridge")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private ChatService chatService;

    /* =========================
       공통: 로그인 사용자 ID 조회
       ========================= */
    private Long getUserId(HttpSession session) throws UnauthorizedAccessException {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null || user.getId() == null) {
            throw new UnauthorizedAccessException("로그인된 사용자 정보가 없습니다.");
        }
        return user.getId();
    }

    /* =========================
       📄 냉장고 페이지
       ========================= */
    @GetMapping("/list")
    public String fridgeList(HttpSession session, Model model) {
        try {
            getUserId(session);
            model.addAttribute("fridgeDto", new FridgeDto());
            return "fridge/list";
        } catch (UnauthorizedAccessException e) {
            return "redirect:/user/loginForm";
        }
    }

    /* =========================
       📦 재료 조회
       ========================= */
    @GetMapping("/data")
    @ResponseBody
    public List<FridgeDto> getFridgeData(HttpSession session)
            throws SQLException, UnauthorizedAccessException {

        Long userId = getUserId(session);
        return fridgeService.getIngredientsByUserId(userId);
    }

    /* =========================
       ➕ 재료 등록
       ========================= */
    @PostMapping("/registAjax")
    @ResponseBody
    public Map<String, Object> registIngredientAjax(
            @Valid FridgeDto fridgeDto,
            BindingResult result,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserId(session);
            fridgeDto.setUserId(userId);

            if (result.hasErrors()) {
            response.put("status", "FAIL");
            response.put(
                "message",
                result.getAllErrors().get(0).getDefaultMessage()
            );
            return response;
}

            fridgeService.registIngredient(fridgeDto);
            response.put("status", "OK");
            response.put("message", "재료가 등록되었습니다.");

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        }
        return response;
    }

    /* =========================
       ❌ 재료 단일 삭제
       ========================= */
    @PostMapping("/removeAjax")
    @ResponseBody
    public Map<String, Object> removeIngredientAjax(
            @RequestParam("id") Long ingredientId,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(session);
            fridgeService.removeIngredient(ingredientId, userId);
            response.put("status", "OK");
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        }
        return response;
    }

    /* =========================
       ❌ 재료 다중 삭제 (JS 대응)
       ========================= */
    @PostMapping("/removeMultipleAjax")
    @ResponseBody
    public Map<String, Object> removeMultipleAjax(
            @RequestParam("ids[]") List<Long> ids,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(session);

            for (Long id : ids) {
                fridgeService.removeIngredient(id, userId);
            }

            response.put("status", "OK");
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        }
        return response;
    }

    /* =========================
       🧠🔥 냉장고 기반 AI 추천
       (JS에서 호출하는 엔드포인트)
       ========================= */
    @PostMapping("/recommend")
    @ResponseBody
    public Map<String, Object> recommendFromFridge(
            @RequestParam(value = "ids[]", required = false) List<Long> ids,
            HttpSession session
    ) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1️⃣ 로그인 사용자
            Long userId = getUserId(session);

            // 2️⃣ 선택 재료 / 전체 재료 분기
            List<String> ingredients;
            if (ids != null && !ids.isEmpty()) {
                ingredients = fridgeService.getIngredientNamesByIds(userId, ids);
            } else {
                ingredients = fridgeService.getIngredientNames(userId);
            }

            if (ingredients == null || ingredients.isEmpty()) {
                result.put("status", "OK");
                result.put("recipeId", null);
                result.put("aiMessage", "선택한 재료가 없어요.");
                result.put("recipeQuery", null);
                return result;
            }

            // 3️⃣ FastAPI 요청 바디
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", "user-" + userId);
            requestBody.put("ingredients", ingredients);

            RestTemplate restTemplate = new RestTemplate();

            @SuppressWarnings("unchecked")
            Map<String, Object> fastApiResponse =
                    restTemplate.postForObject(
                            "http://192.168.0.7:8000/api/recommend/fridge",
                            requestBody,
                            Map.class
                    );
            System.out.println("🔥 FASTAPI RESPONSE = " + fastApiResponse);

            // 4️⃣ FastAPI 응답 NULL 방어
            if (fastApiResponse == null) {
                result.put("status", "ERROR");
                result.put("recipeId", null);
                result.put("aiMessage", "AI 서버 응답이 없습니다.");
                result.put("recipeQuery", null);
                return result;
            }

            // =========================
            // 🔥 핵심: 키 이름 불일치 대응
            // =========================
            Object recipeIdObj =
                    fastApiResponse.get("recipe_id") != null
                            ? fastApiResponse.get("recipe_id")
                            : fastApiResponse.get("recipeId");

            Object answerObj =
                    fastApiResponse.get("answer") != null
                            ? fastApiResponse.get("answer")
                            : fastApiResponse.get("aiMessage");

            // 5️⃣ 레시피 없음
            if (recipeIdObj == null) {
                result.put("status", "OK");
                result.put("recipeId", null);
                result.put(
                        "aiMessage",
                        answerObj != null
                                ? answerObj
                                : "해당 재료로 만들 수 있는 레시피를 찾지 못했어요."
                );
                result.put("recipeQuery", null);
                return result;
            }

            // 6️⃣ 정상 응답
            result.put("status", "OK");
            result.put("recipeId", recipeIdObj);
            result.put(
                    "aiMessage",
                    answerObj != null
                            ? answerObj
                            : "냉장고 재료로 레시피를 추천했어요!"
            );
            result.put("recipeQuery", null);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "ERROR");
            result.put("recipeId", null);
            result.put("aiMessage", "레시피 추천 중 오류가 발생했습니다.");
            result.put("recipeQuery", null);
            return result;
        }
    }
    /* =========================
       🧠🔥 (기존) AI 추천 API
       ========================= */
    @PostMapping("/ai-recommend")
    @ResponseBody
    public ChatResponse recommendFromFridgeApi(HttpSession session)
            throws SQLException {

        Long userId = getUserId(session);
        return chatService.recommendFromFridge(userId);
    }

    /* =========================
       🧾 추천 결과 상세 페이지
       ========================= */
    @GetMapping("/recipe-detail")
    public String fridgeRecipeDetail() {
        return "fridge/recipe-detail";
    }

    @PostMapping("/modifyAjax")
@ResponseBody
public Map<String, Object> modifyIngredientAjax(
        @Valid FridgeDto fridgeDto,
        BindingResult result,
        HttpSession session
) {
    Map<String, Object> response = new HashMap<>();

    try {
        Long userId = getUserId(session);
        fridgeDto.setUserId(userId);

        // ✅ 검증 에러 처리 (NPE 방지)
        if (result.hasErrors()) {
            response.put("status", "FAIL");
            response.put("message", result.getAllErrors().get(0).getDefaultMessage());
            return response;
        }

        // ✅ 수정하려면 id가 꼭 있어야 함
        if (fridgeDto.getId() == null) {
            response.put("status", "FAIL");
            response.put("message", "수정할 재료 ID가 없습니다.");
            return response;
        }

        fridgeService.modifyIngredient(fridgeDto);

        response.put("status", "OK");
        response.put("message", "재료가 수정되었습니다.");
        return response;

    } catch (Exception e) {
        response.put("status", "ERROR");
        response.put("message", e.getMessage());
        return response;
    }
}
}