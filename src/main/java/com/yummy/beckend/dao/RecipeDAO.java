package com.yummy.beckend.dao;

import com.yummy.beckend.dto.RecipeDto;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface RecipeDAO {
    // 통합된 목록 조회 (검색+카테고리+페이징)
    List<RecipeDto> selectRecipeList(Map<String, Object> params) throws SQLException;
    
    // 전체 개수 조회 (페이징용)
    int countRecipeList(Map<String, Object> params) throws SQLException;

    // 기존 메서드 유지
    RecipeDto findById(Long recipeId, Long userId) throws SQLException;
    void insertFavorite(Long userId, Long recipeId) throws SQLException;
    void deleteFavorite(Long userId, Long recipeId) throws SQLException;
    int countFavorite(Long userId, Long recipeId) throws SQLException;
    List<RecipeDto> findFavoritesByUserId(Long userId) throws SQLException;
    List<String> findTypeCategoriesByRecipeId(Long recipeId) throws SQLException;
    List<String> findMethodCategoriesByRecipeId(Long recipeId) throws SQLException;
    
    // 기존 findAll, findByNameKeyword는 selectRecipeList로 대체되므로 삭제하거나 유지해도 됨
}