package com.yummy.beckend.dao;

import com.yummy.beckend.dto.RecipeDto;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface RecipeDAO {

    List<RecipeDto> findAll() throws SQLException;
    List<RecipeDto> findByNameKeyword(String keyword) throws SQLException;
    RecipeDto findById(Long recipeId, Long userId) throws SQLException;
    
    // 즐겨찾기 관련
    void insertFavorite(Long userId, Long recipeId) throws SQLException;
    void deleteFavorite(Long userId, Long recipeId) throws SQLException;
    int countFavorite(Long userId, Long recipeId) throws SQLException;
    List<RecipeDto> findFavoritesByUserId(Long userId) throws SQLException;

    // ⭐️ [복구됨] 상세 조회 시 카테고리 이름을 가져오기 위한 메서드
    List<String> findTypeCategoriesByRecipeId(Long recipeId) throws SQLException;
    List<String> findMethodCategoriesByRecipeId(Long recipeId) throws SQLException;
}