package com.yummy.beckend.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yummy.beckend.dto.FridgeDto;
import com.yummy.beckend.exception.UnauthorizedAccessException;

public interface FridgeService {

    // B-2. 재료 등록
    void registIngredient(FridgeDto fridgeDto) throws SQLException;

    // B-2. 재료 수정
    void modifyIngredient(FridgeDto fridgeDto) throws SQLException, UnauthorizedAccessException;

    // B-2. 재료 삭제
    void removeIngredient(Long ingredientId, Long userId) throws SQLException, UnauthorizedAccessException;
    
    // D-2. 사용자별 냉장고 재료 목록 조회 (D-day 및 경고 정보 포함)
    List<FridgeDto> getIngredientsByUserId(Long userId) throws SQLException;
    
    // B-1. 재료 활용 레시피 검색을 위한 재료명 리스트 조회
    List<String> getIngredientNamesForRecipeSearch(Long userId) throws SQLException;

    // FridgeService.java Interface 추가
void removeIngredients(List<Long> idList, Long userId) throws SQLException;


}