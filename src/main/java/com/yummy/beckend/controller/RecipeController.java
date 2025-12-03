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

import com.yummy.beckend.dto.RecipeDto;
import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.service.RecipeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Tag(name = "4. 레시피 & 즐겨찾기", description = "레시피 검색, 상세 조회, 즐겨찾기")
@Controller
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    private Long getUserId(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        return (user != null) ? user.getId() : null;
    }

    @Operation(summary = "레시피 검색 페이지", description = "키워드로 레시피를 검색하거나 전체 목록을 조회합니다.")
    @GetMapping("/search")
    public String searchPage(
        @Parameter(description = "검색어") @RequestParam(value = "keyword", required = false) String keyword,
        @Parameter(hidden = true) Model model) throws SQLException {
        
        List<RecipeDto> recipeList = new ArrayList<>();
        String searchMessage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            recipeList = recipeService.searchRecipes(keyword);
            searchMessage = "'" + keyword + "' 검색 결과 (" + recipeList.size() + "건)";
        } else {
            recipeList = recipeService.getAllRecipes(); 
            searchMessage = "전체 레시피 목록";
        }
        
        model.addAttribute("keyword", keyword);
        model.addAttribute("recipeList", recipeList);
        model.addAttribute("searchMessage", searchMessage);
        
        return "recipe/search"; 
    }

    @Operation(summary = "레시피 상세 조회", description = "레시피 ID로 상세 정보를 조회합니다.")
    @GetMapping("/detail/{recipeId}")
    public String recipeDetail(@Parameter(description = "레시피 ID") @PathVariable Long recipeId, 
                               @Parameter(hidden = true) HttpSession session, 
                               @Parameter(hidden = true) Model model) throws SQLException {
        Long userId = getUserId(session);
        RecipeDto recipe = recipeService.getRecipeDetail(recipeId, userId);
        
        if (recipe == null) {
            return "redirect:/recipe/search"; 
        }
        
        model.addAttribute("recipe", recipe);
        return "recipe/detail"; 
    }
    
    @Operation(summary = "즐겨찾기 목록 페이지", description = "로그인한 사용자의 즐겨찾기 목록을 보여줍니다.")
    @GetMapping("/favorites")
    public String favoriteList(@Parameter(hidden = true) HttpSession session, 
                               @Parameter(hidden = true) Model model) throws SQLException {
        Long userId = getUserId(session);
        if (userId == null) {
            return "redirect:/user/loginForm";
        }
        
        List<RecipeDto> favorites = recipeService.getFavoriteRecipes(userId);
        
        model.addAttribute("recipeList", favorites);
        model.addAttribute("searchMessage", "나의 즐겨찾기 목록");
        model.addAttribute("keyword", ""); 
        
        return "recipe/search"; 
    }

    @Operation(summary = "즐겨찾기 토글 (AJAX)", description = "즐겨찾기 추가/삭제를 토글합니다. (return JSON)")
    @PostMapping("/toggleFavorite")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@Parameter(description = "레시피 ID") @RequestParam("recipeId") Long recipeId, 
                                              @Parameter(hidden = true) HttpSession session,
                                              @Parameter(hidden = true) HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Long userId = getUserId(session);

        if (userId == null) {
            String referer = request.getHeader("Referer");
            if (referer != null) session.setAttribute("prevPage", referer);
            
            response.put("status", "UNAUTHORIZED");
            response.put("message", "로그인 후 이용 가능합니다.");
            response.put("redirectUrl", "/user/loginForm");
            return response;
        }

        try {
            recipeService.toggleFavorite(userId, recipeId);
            int currentCount = recipeService.countFavorite(userId, recipeId);
            boolean isFavorite = currentCount > 0;
            
            response.put("status", "OK");
            response.put("isFavorite", isFavorite);
            response.put("message", isFavorite ? "즐겨찾기에 추가되었습니다." : "즐겨찾기에서 삭제되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "서버 오류가 발생했습니다.");
        }
        return response;
    }
}