package com.yummy.beckend.service;

import com.yummy.beckend.dto.CategoryDto;
import com.yummy.beckend.dto.RecipeDto;
import java.sql.SQLException;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllTypeCategories() throws SQLException;
    List<CategoryDto> getAllMethodCategories() throws SQLException;
    
    // 다중 필터링 메서드
    List<RecipeDto> getRecipesByTypeAndMethod(Long typeCatId, Long methodCatId) throws SQLException;
}