package com.yummy.beckend.dao;

import com.yummy.beckend.dto.CategoryDto;
import com.yummy.beckend.dto.RecipeDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CategoryDAOImpl implements CategoryDAO {

    @Autowired
        private SqlSession sqlSession;

    private static final String NAMESPACE = "Category-Mapper"; 

    @Override
    public List<CategoryDto> findAllTypeCategories() throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findAllTypeCategories");
    }

    @Override
    public List<CategoryDto> findAllMethodCategories() throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findAllMethodCategories");
    }
    
    // @Override
    // public List<RecipeDto> findRecipesByTypeAndMethod(Long typeCatId, Long methodCatId) throws SQLException {
    //     Map<String, Object> map = new HashMap<>();
    //     map.put("typeCatId", typeCatId);
    //     map.put("methodCatId", methodCatId);
    //     return sqlSession.selectList(NAMESPACE + ".findRecipesByTypeAndMethod", map);
    // }
}