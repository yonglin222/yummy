package com.yummy.beckend.dao;

import com.yummy.beckend.dto.RecipeDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Repository
public class RecipeDAOImpl implements RecipeDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String NAMESPACE = "Recipe-Mapper";

    // 통합된 목록 조회 (검색+카테고리+페이징)
    @Override
    public List<RecipeDto> selectRecipeList(Map<String, Object> params) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".selectRecipeList", params);
    }

    // 전체 개수 조회 (페이징용)
    @Override
    public int countRecipeList(Map<String, Object> params) throws SQLException {
        return sqlSession.selectOne(NAMESPACE + ".countRecipeList", params);
    }
    
    @Override
    public RecipeDto findById(Long recipeId, Long userId) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("recipeId", recipeId);
        map.put("userId", userId);
        return sqlSession.selectOne(NAMESPACE + ".findById", map);
    }

    // 즐겨찾기 관련 메서드
    @Override
    public void insertFavorite(Long userId, Long recipeId) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("recipeId", recipeId);
        sqlSession.insert(NAMESPACE + ".insertFavorite", map);
    }

    @Override
    public void deleteFavorite(Long userId, Long recipeId) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("recipeId", recipeId);
        sqlSession.delete(NAMESPACE + ".deleteFavorite", map);
    }

    @Override
    public int countFavorite(Long userId, Long recipeId) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("recipeId", recipeId);
        return sqlSession.selectOne(NAMESPACE + ".countFavorite", map);
    }

    // 즐겨찾기 목록 조회
    @Override
    public List<RecipeDto> findFavoritesByUserId(Long userId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findFavoritesByUserId", userId);
    }
    
    // 조리 순서 및 카테고리 조회 메서드
    @Override
    public List<String> findTypeCategoriesByRecipeId(Long recipeId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findTypeCategoriesByRecipeId", recipeId);
    }
    
    @Override
    public List<String> findMethodCategoriesByRecipeId(Long recipeId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findMethodCategoriesByRecipeId", recipeId);
    }
}