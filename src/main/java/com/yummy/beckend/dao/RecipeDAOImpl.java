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

    @Override
    public List<RecipeDto> findAll() throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findAll");
    }

    @Override
    public List<RecipeDto> findByNameKeyword(String keyword) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findByNameKeyword", keyword);
    }
    
    @Override
    public RecipeDto findById(Long recipeId, Long userId) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("recipeId", recipeId);
        map.put("userId", userId);
        return sqlSession.selectOne(NAMESPACE + ".findById", map);
    }

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

    @Override
    public List<RecipeDto> findFavoritesByUserId(Long userId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findFavoritesByUserId", userId);
    }

    // ⭐️ [복구됨] 상세 조회용 카테고리 목록 조회 구현
    @Override
    public List<String> findTypeCategoriesByRecipeId(Long recipeId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findTypeCategoriesByRecipeId", recipeId);
    }

    @Override
    public List<String> findMethodCategoriesByRecipeId(Long recipeId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findMethodCategoriesByRecipeId", recipeId);
    }
}