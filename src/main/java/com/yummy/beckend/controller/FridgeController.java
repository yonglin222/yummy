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

import com.yummy.beckend.dto.ChatResponse;
import com.yummy.beckend.dto.FridgeDto;
import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.exception.UnauthorizedAccessException;
import com.yummy.beckend.service.ChatService; // 추가
import com.yummy.beckend.service.FridgeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.sql.SQLException;
import java.util.Arrays; // 추가
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // 추가

@Tag(name = "2. 냉장고 관리", description = "재료 등록, 수정, 삭제, 조회 (AJAX)")
@Controller
@RequestMapping("/fridge")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private ChatService chatService; // ChatService 주입

    private Long getUserId(HttpSession session) throws UnauthorizedAccessException {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null || user.getId() == null) {
            throw new UnauthorizedAccessException("로그인된 사용자 정보가 없습니다.");
        }
        return user.getId();
    }

    @Operation(summary = "냉장고 목록 페이지", description = "냉장고 관리 HTML 페이지를 반환합니다.")
    @GetMapping("/list")
    public String fridgeList(@Parameter(hidden = true) HttpSession session,
                             @Parameter(hidden = true) Model model) {
        try {
            Long userId = getUserId(session);
            model.addAttribute("fridgeDto", new FridgeDto());
            return "fridge/list";
        } catch (UnauthorizedAccessException e) {
            return "redirect:/user/loginForm";
        }
    }

    @Operation(summary = "재료 목록 조회 (AJAX)", description = "로그인한 사용자의 모든 재료 목록을 JSON으로 반환합니다.")
    @GetMapping("/data")
    @ResponseBody
    public List<FridgeDto> getFridgeData(@Parameter(hidden = true) HttpSession session) throws SQLException, UnauthorizedAccessException {
        Long userId = getUserId(session);
        return fridgeService.getIngredientsByUserId(userId);
    }

    @Operation(summary = "재료 등록 (AJAX)", description = "새로운 재료를 등록합니다.")
    @PostMapping("/registAjax")
    @ResponseBody
    public Map<String, Object> registIngredientAjax(@Valid FridgeDto fridgeDto,
                                                    @Parameter(hidden = true) BindingResult result,
                                                    @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserId(session);
            fridgeDto.setUserId(userId);

            if (result.hasErrors()) {
                response.put("status", "FAIL");
                response.put("message", result.getFieldError().getDefaultMessage());
                response.put("errors", result.getAllErrors());
                return response;
            }

            fridgeService.registIngredient(fridgeDto);

            response.put("status", "OK");
            response.put("message", "재료 '" + fridgeDto.getIngredient() + "'가 등록되었습니다.");

        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", "로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "데이터베이스 오류가 발생했습니다.");
        }

        return response;
    }

    @Operation(summary = "재료 삭제 (AJAX)", description = "특정 ID의 재료를 삭제합니다.")
    @PostMapping("/removeAjax")
    @ResponseBody
    public Map<String, Object> removeIngredientAjax(@Parameter(description = "삭제할 재료 ID") @RequestParam("id") Long ingredientId,
                                                    @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserId(session);
            fridgeService.removeIngredient(ingredientId, userId);
            response.put("status", "OK");
            response.put("message", "재료가 삭제되었습니다.");

        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "데이터베이스 오류가 발생했습니다.");
        }

        return response;
    }

    @Operation(summary = "재료 수정 (AJAX)", description = "재료 정보를 수정합니다.")
    @PostMapping("/modifyAjax")
    @ResponseBody
    public Map<String, Object> modifyIngredientAjax(@Valid FridgeDto fridgeDto,
                                                    @Parameter(hidden = true) BindingResult result,
                                                    @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserId(session);
            fridgeDto.setUserId(userId);

            if (result.hasErrors()) {
                response.put("status", "FAIL");
                response.put("message", result.getFieldError().getDefaultMessage());
                return response;
            }

            fridgeService.modifyIngredient(fridgeDto);
            response.put("status", "OK");
            response.put("message", "재료 정보가 수정되었습니다.");

        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", "수정 권한이 없습니다: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "재료 수정 중 데이터베이스 오류가 발생했습니다.");
        }

        return response;
    }

    @Operation(summary = "재료 다중 삭제 (AJAX)", description = "선택한 여러 재료를 한 번에 삭제합니다.")
    @PostMapping("/removeMultipleAjax")
    @ResponseBody
    public Map<String, Object> removeMultipleIngredients(@Parameter(description = "삭제할 재료 ID 리스트") @RequestParam(value = "ids[]") List<Long> ids,
                                                         @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(session);

            if (ids == null || ids.isEmpty()) {
                response.put("status", "FAIL");
                response.put("message", "삭제할 재료가 선택되지 않았습니다.");
                return response;
            }

            fridgeService.removeIngredients(ids, userId);
            response.put("status", "OK");
            response.put("message", ids.size() + "개의 재료가 삭제되었습니다.");

        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", "로그인 세션이 만료되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "삭제 중 오류가 발생했습니다.");
        }
        return response;
    }

    // ===============================
    // 6️⃣ 냉장고 재료 기반 AI 추천 시작 (수정된 기능: JSON 반환)
    // ===============================
    @Operation(summary = "냉장고 재료 기반 AI 추천 시작", description = "선택 재료 또는 전체 재료로 AI에게 레시피를 추천받아 JSON 결과를 반환합니다.")
    @PostMapping("/recommend")
    @ResponseBody
    public Map<String, Object> recommendRecipeByFridge(
            @Parameter(description = "선택된 재료 ID 목록 (콤마로 구분, 전체 재료 선택 시 빈 문자열)")
            @RequestParam(value = "selectedIds", required = false, defaultValue = "") String selectedIds,
            @Parameter(description = "재추천 시 사용할 이전 AI 쿼리 텍스트 (URL 디코딩 필요)")
            @RequestParam(value = "recipeQuery", required = false) String recipeQueryText, // ⭐️ 새 파라미터 추가
            @Parameter(hidden = true) HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserId(session);
            String ingredientQuery = "";
            String userMessage = "";
            String aiUserId = userId.toString();

            // ⭐️⭐️⭐️ 1. 재추천 쿼리 텍스트가 있으면 그걸 사용 (수정된 로직) ⭐️⭐️⭐️
            if (recipeQueryText != null && !recipeQueryText.isEmpty()) {
                // 재추천 버튼 클릭 시, 이미 생성된 쿼리를 사용
                userMessage = recipeQueryText;
                // ingredientQuery에는 재추천을 위해 사용된 원본 재료 텍스트를 다시 저장
                // (프롬프트에서 "냉장고 재료: " 부분을 잘라내야 하지만, 여기서는 간단히 전체 텍스트를 저장)
                ingredientQuery = recipeQueryText.replace("냉장고 재료: ", "").replace(", 이 재료들을 활용한 레시피 알려줘.", "");
            } else {
                // 1-1. 재료 쿼리 생성 (선택 또는 전체)
                if (selectedIds != null && !selectedIds.isEmpty()) {
                    List<Long> idList = Arrays.stream(selectedIds.split(","))
                                              .map(Long::valueOf)
                                              .collect(Collectors.toList());
                    ingredientQuery = fridgeService.getIngredientNamesByIds(idList, userId);
                } else {
                    ingredientQuery = fridgeService.getAllIngredientNamesAsString(userId);
                }

                // 1-2. 프롬프트 구조 생성
                if (ingredientQuery.isEmpty()) {
                    userMessage = "냉장고에 재료가 없으니, 내가 좋아하는 재료를 포함한 레시피를 추천해줘.";
                } else {
                    userMessage = "냉장고 재료: " + ingredientQuery + ", 이 재료들을 활용한 레시피 알려줘.";
                }
            }
            // ⭐️⭐️⭐️ 로직 수정 끝 ⭐️⭐️⭐️
            
            // 2. AI 서비스 호출
            // ... (기존 코드 유지) ...
            ChatResponse chatResponse = chatService.chat(
                userMessage,
                aiUserId
            );

            // 3. 응답 객체 구성
            response.put("status", "OK");
            response.put("recipeId", chatResponse.getRecommendedRecipe() != null ? chatResponse.getRecommendedRecipe().getRecipeId() : null);
            response.put("aiMessage", chatResponse.getResponse());
            response.put("recipeQuery", userMessage); // ⭐️ AI에게 최종적으로 전송한 프롬프트를 반환
            
            // ... (기존 코드 유지) ...
        } catch (UnauthorizedAccessException e) {
            // ... (기존 코드 유지) ...
        } catch (Exception e) {
            // ... (기존 코드 유지) ...
        }

        return response;
    }
    
    // ===============================
    // 7️⃣ AI 레시피 상세 페이지 이동 (추가된 기능)
    // ===============================
    @Operation(summary = "AI 레시피 상세 페이지", description = "AI 추천 레시피 상세 HTML 페이지를 반환합니다. (새 창용)")
    @GetMapping("/recipe-detail")
    public String fridgeRecipeDetail() {
        return "fridge/recipe-detail"; // fridge/recipe-detail.html 렌더링
    }
}