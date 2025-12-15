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
        // DTO에 userId가 설정되어 있다고 가정하고 DAO로 전달
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

    /**
     * D-2. 사용자별 냉장고 재료 목록 조회
     * D-day 계산은 Mapper XML에서 이미 처리하고 있습니다. (TRUNC(EXPIRATION_DATE - SYSDATE))
     * 여기서는 DB에서 가져온 D-day 정보를 활용하여 추가적인 비즈니스 로직(경고 표시)을 적용할 수 있습니다.
     */
    @Override
    public List<FridgeDto> getIngredientsByUserId(Long userId) throws SQLException {
        // DAO에서 D-day 정보까지 계산된 FridgeDto 리스트를 가져옵니다.
        List<FridgeDto> ingredients = fridgeDAO.findByUserId(userId);
        
        // **B-2. 유통기한 임박 재료 경고문 로직은 Frontend에서 D-day를 활용해 표시하는 것이 일반적이지만,
        // Service 단에서 추가적인 처리가 필요할 경우 여기서 구현 가능합니다.**
        
        // 예: D-day가 3일 이하인 경우 DTO에 경고 플래그를 추가하는 등. (현재 DTO에 경고 필드는 없으므로 생략)
        
        return ingredients;
    }

    /**
     * B-1. 재료 활용 레시피 검색을 위한 재료명 리스트 조회
     */
    @Override
    public List<String> getIngredientNamesForRecipeSearch(Long userId) throws SQLException {
        return fridgeDAO.findIngredientNamesByUserId(userId);
    }
    // FridgeServiceImpl.java 구현체 추가
    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void removeIngredients(List<Long> idList, Long userId) throws SQLException {
        if (idList == null || idList.isEmpty()) return;
        // 일괄 삭제 호출
        fridgeDAO.deleteIngredients(userId, idList);
    }

    
}