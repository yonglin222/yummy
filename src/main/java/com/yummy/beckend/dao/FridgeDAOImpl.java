package com.yummy.beckend.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.yummy.beckend.dto.FridgeDto;

@Repository
public class FridgeDAOImpl implements FridgeDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "Fridge-Mapper"; // 새로운 매퍼 네임스페이스 정의

    @Override
    public void insertIngredient(FridgeDto fridgeDto) throws SQLException {
        sqlSession.insert(NAMESPACE + ".insertIngredient", fridgeDto);
    }

    @Override
    public void updateIngredient(FridgeDto fridgeDto) throws SQLException {
        sqlSession.update(NAMESPACE + ".updateIngredient", fridgeDto);
    }

    @Override
    public void deleteIngredient(Long id) throws SQLException {
        sqlSession.delete(NAMESPACE + ".deleteIngredient", id);
    }

    @Override
    public List<FridgeDto> findByUserId(Long userId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findByUserId", userId);
    }

    @Override
    public FridgeDto findById(Long id) throws SQLException {
        return sqlSession.selectOne(NAMESPACE + ".findById", id);
    }
    
    @Override
    public List<String> findIngredientNamesByUserId(Long userId) throws SQLException {
        return sqlSession.selectList(NAMESPACE + ".findIngredientNamesByUserId", userId);
    }

    @Override
public void deleteIngredients(Long userId, List<Long> idList) throws SQLException {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("idList", idList);
    sqlSession.delete("Fridge-Mapper.deleteIngredients", params); 
    }
}
