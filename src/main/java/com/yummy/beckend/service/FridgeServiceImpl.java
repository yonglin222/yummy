package com.yummy.beckend.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yummy.beckend.dao.FridgeDAO;
import com.yummy.beckend.dto.FridgeDto;
import com.yummy.beckend.exception.UnauthorizedAccessException;

@Service
public class FridgeServiceImpl implements FridgeService {

    @Autowired
    private FridgeDAO fridgeDAO;

    // 이 Service는 Session에서 받은 userId를 기반으로 작동

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void registIngredient(FridgeDto fridgeDto) throws SQLException {
        fridgeDAO.insertIngredient(fridgeDto);
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void modifyIngredient(FridgeDto fridgeDto) throws SQLException, UnauthorizedAccessException {
        // 1. 수정 전 해당 재료의 소유권 확인
        FridgeDto existing = fridgeDAO.findById(fridgeDto.getId());

        if (existing == null || !existing.getUserId().equals(fridgeDto.getUserId())) {
            // 메시지: "해당 재료를 수정할 권한이 없습니다."
            throw new UnauthorizedAccessException("수정 권한이 없는 재료입니다.");
        }

        // 2. 재료 수정
        fridgeDAO.updateIngredient(fridgeDto);
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void removeIngredient(Long ingredientId, Long userId) throws SQLException, UnauthorizedAccessException {
        // 1. 삭제 전 해당 재료의 소유권 확인
        FridgeDto existing = fridgeDAO.findById(ingredientId);

        if (existing == null || !existing.getUserId().equals(userId)) {
            // 메시지: "해당 재료를 삭제할 권한이 없습니다."
            throw new UnauthorizedAccessException("삭제 권한이 없는 재료입니다.");
        }

        // 2. 재료 삭제
        fridgeDAO.deleteIngredient(ingredientId);
    }

    // D-2. 사용자별 냉장고 재료 목록 조회
    @Override
    public List<FridgeDto> getIngredientsByUserId(Long userId) throws SQLException {
        // DAO에서 D-day 정보까지 계산된 FridgeDto 리스트를 가져옴
        List<FridgeDto> ingredients = fridgeDAO.findByUserId(userId);

        return ingredients;
    }

    // B-1. 재료 활용 레시피 검색을 위한 재료명 리스트 조회
    @Override
    public List<String> getIngredientNamesForRecipeSearch(Long userId) throws SQLException {
        return fridgeDAO.findIngredientNamesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void removeIngredients(List<Long> idList, Long userId) throws SQLException {
        if (idList == null || idList.isEmpty()) return;
        // 일괄 삭제 호출
        fridgeDAO.deleteIngredients(userId, idList);
    }

    // B-1. 전체 재료명 리스트를 텍스트로 반환
    @Override
    public String getAllIngredientNamesAsString(Long userId) throws SQLException {
        List<String> names = fridgeDAO.findIngredientNamesByUserId(userId);
        return String.join(", ", names);
    }

    // B-1. 선택된 재료 ID 목록으로 재료명 리스트를 텍스트로 반환
    @Override
    public String getIngredientNamesByIds(List<Long> idList, Long userId) throws SQLException, UnauthorizedAccessException {
        List<String> names = fridgeDAO.findIngredientNamesByIds(idList, userId);

        // DAO에서 이미 userId로 필터링되므로, 여기에 반환된 names는 사용자 소유의 재료명만 포함
        return String.join(", ", names);
    }
}