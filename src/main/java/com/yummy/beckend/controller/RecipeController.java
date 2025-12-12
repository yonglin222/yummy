package com.yummy.beckend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yummy.beckend.dto.CategoryDto;
import com.yummy.beckend.dto.RecipeDto;
import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.service.CategoryService;
import com.yummy.beckend.service.RecipeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "4. 레시피 & 즐겨찾기", description = "레시피 통합 목록, 상세 조회, 즐겨찾기 기능")
@Controller
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CategoryService categoryService;

    private Long getUserId(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        return (user != null) ? user.getId() : null;
    }

    // ==========================================
    // 1. 레시피 목록 (GET)
    // ==========================================
    @Operation(summary = "레시피 목록 페이지")
    @GetMapping("/list")
    public String recipeList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "typeCatId", defaultValue = "0") Long typeCatId,
            @RequestParam(value = "methodCatId", defaultValue = "0") Long methodCatId,
            Model model) throws SQLException {

        int pageSize = 12;
        List<RecipeDto> recipeList = recipeService.getRecipeList(page, pageSize, keyword, typeCatId, methodCatId);
        int totalCount = recipeService.getRecipeCount(keyword, typeCatId, methodCatId);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        int pageBlockSize = 5;
        int startPage = ((page - 1) / pageBlockSize) * pageBlockSize + 1;
        int endPage = startPage + pageBlockSize - 1;
        if (endPage > totalPages) endPage = totalPages;

        List<CategoryDto> typeCategories = categoryService.getAllTypeCategories();
        List<CategoryDto> methodCategories = categoryService.getAllMethodCategories();

        model.addAttribute("recipeList", recipeList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedTypeCatId", typeCatId);
        model.addAttribute("selectedMethodCatId", methodCatId);
        model.addAttribute("typeCategories", typeCategories);
        model.addAttribute("methodCategories", methodCategories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalCount", totalCount);

        String searchMessage = "전체 레시피";
        if (keyword != null && !keyword.isEmpty()) {
            searchMessage = (totalCount == 0) ? "'" + keyword + "'에 대한 검색 결과가 없습니다." : "'" + keyword + "' 검색 결과";
        } else if (typeCatId != 0 || methodCatId != 0) {
            searchMessage = (totalCount == 0) ? "해당 카테고리에 등록된 레시피가 없습니다." : "카테고리별 조회";
        }
        model.addAttribute("searchMessage", searchMessage);

        return "recipe/list";
    }

    // ==========================================
    // 2. 레시피 상세 (GET) -> 여기가 없으면 405 발생 가능
    // ==========================================
    @Operation(summary = "레시피 상세 페이지")
    @GetMapping("/detail/{recipeId}")
    public String recipeDetail(@PathVariable Long recipeId,
                               HttpSession session,
                               Model model) throws SQLException {
        Long userId = getUserId(session);
        RecipeDto recipe = recipeService.getRecipeDetail(recipeId, userId);

        if (recipe == null) {
            return "redirect:/recipe/list";
        }

        // 즐겨찾기 여부 확인
        boolean isFavorite = false;
        if (userId != null) {
            int count = recipeService.countFavorite(userId, recipeId);
            isFavorite = (count > 0);
        }

        model.addAttribute("recipe", recipe);
        model.addAttribute("isFavorite", isFavorite); // 상세 페이지에서 별 모양 표시용
        
        return "recipe/detail";
    }

    // ==========================================
    // 3. 즐겨찾기 목록 (GET)
    // ==========================================
    @Operation(summary = "나의 즐겨찾기 페이지")
    @GetMapping("/favorites")
    public String favoriteList(HttpSession session, Model model) throws SQLException {
        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/user/loginForm";
        }

        List<RecipeDto> favorites = recipeService.getFavoriteRecipes(userId);

        model.addAttribute("recipeList", favorites);
        model.addAttribute("keyword", "");
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("startPage", 1);
        model.addAttribute("endPage", 1);
        model.addAttribute("totalCount", favorites.size());
        
        if (favorites.isEmpty()) {
            model.addAttribute("searchMessage", "즐겨찾기한 레시피가 없습니다.");
        } else {
            model.addAttribute("searchMessage", "나의 즐겨찾기 목록");
        }

        return "recipe/favorites";
    }

    // // ==========================================
    // // 4. 즐겨찾기 토글 (POST) -> 주소창에 치면 안 됨 (버튼 클릭용)
    // // ==========================================

    // ==========================================
    // 4. 즐겨찾기 토글 (POST) -> 수정됨
    // ==========================================
    @Operation(summary = "즐겨찾기 추가/삭제 (AJAX)")
    @PostMapping("/toggleFavorite")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@RequestParam("recipeId") Long recipeId,
                                              HttpSession session,
                                              HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserId(session);

        // [수정 포인트] : 로그인 안 된 상태
        if (userId == null) {
            // 메시지와 리다이렉트 URL을 제거하고, 상태값만 명확히 전달
            response.put("status", "UNAUTHORIZED"); 
            return response; 
        }

        try {
            recipeService.toggleFavorite(userId, recipeId);
            int currentCount = recipeService.countFavorite(userId, recipeId);
            boolean isFavorite = currentCount > 0;

            response.put("status", "OK");
            response.put("isFavorite", isFavorite);
            // 성공 시 메시지도 굳이 필요 없다면 제거해도 되지만, 
            // 토스트 팝업(잠깐 떴다 사라지는 알림)용으로 남겨두는 경우가 많습니다.
            response.put("message", isFavorite ? "즐겨찾기에 추가되었습니다." : "즐겨찾기에서 삭제되었습니다.");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            // 에러 상황은 로그인이랑 다르므로 메시지를 남기는 것이 디버깅에 좋습니다.
            response.put("message", "서버 오류가 발생했습니다.");
        }
        return response;
    }
    // @Operation(summary = "즐겨찾기 추가/삭제 (AJAX)")
    // @PostMapping("/toggleFavorite")
    // @ResponseBody
    // public Map<String, Object> toggleFavorite(@RequestParam("recipeId") Long recipeId,
    //                                           HttpSession session,
    //                                           HttpServletRequest request) {
    //     Map<String, Object> response = new HashMap<>();
    //     Long userId = getUserId(session);

    //     if (userId == null) {
    //         // 로그인 안 된 상태
    //         response.put("status", "UNAUTHORIZED");
    //         response.put("message", "로그인 후 이용 가능합니다.");
    //         response.put("redirectUrl", "/user/loginForm");
    //         return response;
    //     }

    //     try {
    //         recipeService.toggleFavorite(userId, recipeId);
    //         int currentCount = recipeService.countFavorite(userId, recipeId);
    //         boolean isFavorite = currentCount > 0;

    //         response.put("status", "OK");
    //         response.put("isFavorite", isFavorite);
    //         response.put("message", isFavorite ? "즐겨찾기에 추가되었습니다." : "즐겨찾기에서 삭제되었습니다.");
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         response.put("status", "ERROR");
    //         response.put("message", "서버 오류가 발생했습니다.");
    //     }
    //     return response;
    // }
// ==========================================
    // [추가] 5. 레시피 상세 데이터 API (AJAX 모달용)
    // ==========================================
    @Operation(summary = "레시피 상세 데이터 조회 (JSON)")
    @ResponseBody
    @GetMapping("/api/detail/{recipeId}")
    public Map<String, Object> getRecipeDetailApi(@PathVariable Long recipeId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(session);
            
            // 1. 레시피 정보 조회
            RecipeDto recipe = recipeService.getRecipeDetail(recipeId, userId);
            
            // 2. 즐겨찾기 여부 조회
            boolean isFavorite = false;
            if (userId != null) {
                int count = recipeService.countFavorite(userId, recipeId);
                isFavorite = (count > 0);
            }

            response.put("status", "OK");
            response.put("recipe", recipe);
            response.put("isFavorite", isFavorite);

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
        }
        return response;
    }
}