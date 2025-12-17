package com.yummy.beckend.service;

import com.yummy.beckend.dao.RecipeDAO;
import com.yummy.beckend.dto.RecipeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeDAO recipeDAO;

    // 레시피 만드는 법 자동 순번 매기기 로직
    private void processRecipeMethod(RecipeDto recipe) {
        if (recipe == null || recipe.getMethod() == null) {
            recipe.setMethodSteps(new ArrayList<>());
            return;
        }

        // 줄바꿈 문자를 기준으로 텍스트를 분리 (Oracle의 CHR(10) 포함)
        String[] lines = recipe.getMethod().split("(\r\n|\r|\n)");

        List<String> steps = new ArrayList<>();

        for (String line : lines) {
            String trimmedLine = line.trim();

            // 빈 줄이나 주석은 건너뜀
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                continue;
            }

            // 기존의 숫자, 점, 공백 등을 제거하여 순수 텍스트만 추출
            String cleanStep = trimmedLine.replaceAll("^[0-9]\\.?\\s*", "").trim();

            steps.add(cleanStep);
        }

        // DTO에 리스트 설정 (View에서 <ol> 태그로 출력됨)
        recipe.setMethodSteps(steps);
    }

    // 통합 레시피 목록 조회 (검색 + 카테고리 + 페이징)
    @Override
    public List<RecipeDto> getRecipeList(int page, int pageSize, String keyword, Long typeCatId, Long methodCatId) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("typeCatId", typeCatId);
        params.put("methodCatId", methodCatId);

        // Oracle ROWNUM 페이징 계산
        int startRow = (page - 1) * pageSize;
        int endRow = page * pageSize;

        params.put("startRow", startRow);
        params.put("endRow", endRow);

        return recipeDAO.selectRecipeList(params);
    }

    // 전체 레시피 개수 조회 (페이징 버튼 계산용)
    @Override
    public int getRecipeCount(String keyword, Long typeCatId, Long methodCatId) throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("typeCatId", typeCatId);
        params.put("methodCatId", methodCatId);

        return recipeDAO.countRecipeList(params);
    }

    // 레시피 상세 조회
    @Override
    public RecipeDto getRecipeDetail(Long recipeId, Long userId) throws SQLException {
        // 1. 기본 정보 및 즐겨찾기 여부 조회
        RecipeDto recipe = recipeDAO.findById(recipeId, userId);

        if (recipe != null) {
            // 2. 만드는 법 자동 번호 매기기 처리
            processRecipeMethod(recipe);

            // 3. 카테고리 정보 조회 (상세 페이지 표시용)
            List<String> typeCats = recipeDAO.findTypeCategoriesByRecipeId(recipeId);
            List<String> methodCats = recipeDAO.findMethodCategoriesByRecipeId(recipeId);

            recipe.setTypeCategories(typeCats);
            recipe.setMethodCategories(methodCats);
        }

        return recipe;
    }

    // 즐겨찾기 관련 로직(토글)
    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void toggleFavorite(Long userId, Long recipeId) throws SQLException {
        int count = recipeDAO.countFavorite(userId, recipeId);

        if (count > 0) {
            recipeDAO.deleteFavorite(userId, recipeId);
        } else {
            recipeDAO.insertFavorite(userId, recipeId);
        }
    }

    @Override
    public int countFavorite(Long userId, Long recipeId) throws SQLException {
        return recipeDAO.countFavorite(userId, recipeId);
    }

    @Override
    public List<RecipeDto> getFavoriteRecipes(Long userId) throws SQLException {
        return recipeDAO.findFavoritesByUserId(userId);
    }

    // searchRecipes, getAllRecipes는 getRecipeList로 통합되었으므로 제거
    // @Override
    // public List<RecipeDto> searchRecipes(String keyword) throws SQLException {
    //     return getRecipeList(1, 1000, keyword, 0L, 0L);
    // }

    // @Override
    // public List<RecipeDto> getAllRecipes() throws SQLException {
    //     return getRecipeList(1, 1000, null, 0L, 0L);
    // }
}