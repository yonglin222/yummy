package com.yummy.beckend.dao;

import java.sql.SQLException;
import java.util.List;

import com.yummy.beckend.dto.FridgeDto;
import org.apache.ibatis.annotations.Param;

public interface FridgeDAO {

    // B-2. 재료 입력
    void insertIngredient(FridgeDto fridgeDto) throws SQLException;

    // B-2. 재료 수정
    void updateIngredient(FridgeDto fridgeDto) throws SQLException;

    // B-2. 재료 삭제
    void deleteIngredient(Long id) throws SQLException;
    
    // D-2. 냉장고 재료 목록 조회 (사용자별)
    List<FridgeDto> findByUserId(Long userId) throws SQLException;

    // 특정 재료 ID로 재료 정보 조회 (수정/삭제 전 검증용)
    FridgeDto findById(Long id) throws SQLException;
    
    // B-1. 재료 활용 레시피 검색을 위해, 재료명 리스트만 조회
    List<String> findIngredientNamesByUserId(Long userId) throws SQLException;

    void deleteIngredients(@Param("userId") Long userId, @Param("idList") List<Long> idList) throws SQLException;
}