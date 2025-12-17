package com.yummy.beckend.service;

import com.yummy.beckend.dto.RecipeDto;

import java.sql.SQLException;
import java.util.List;

public interface RecipeService {

    // 통합 조회
    List<RecipeDto> getRecipeList(int page, int pageSize, String keyword, Long typeCatId, Long methodCatId) throws SQLException;
    // 전체 개수 조회
    int getRecipeCount(String keyword, Long typeCatId, Long methodCatId) throws SQLException;

    // 키워드 검색
    // List<RecipeDto> searchRecipes(String keyword) throws SQLException;
    
    // 전체 목록 조회
    // List<RecipeDto> getAllRecipes() throws SQLException;
    
    // 레시피 상세 조회 (즐겨찾기 여부 + 카테고리 정보 + 자동 번호 매기기 포함)
    RecipeDto getRecipeDetail(Long recipeId, Long userId) throws SQLException;

    // 즐겨찾기 토글 (추가/삭제)
    void toggleFavorite(Long userId, Long recipeId) throws SQLException;
    
    // 즐겨찾기 개수 확인 (상태 체크용)
    int countFavorite(Long userId, Long recipeId) throws SQLException;
    
    // 사용자별 즐겨찾기 목록 조회
    List<RecipeDto> getFavoriteRecipes(Long userId) throws SQLException;
}