package com.yummy.beckend.dao;

import com.yummy.beckend.dto.CategoryDto;
import com.yummy.beckend.dto.RecipeDto;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface CategoryDAO {

    List<CategoryDto> findAllTypeCategories() throws SQLException;
    List<CategoryDto> findAllMethodCategories() throws SQLException;
    
    // ⭐️ 4가지 케이스를 처리하는 XML 쿼리를 호출
    List<RecipeDto> findRecipesByTypeAndMethod(
        @Param("typeCatId") Long typeCatId,
        @Param("methodCatId") Long methodCatId) throws SQLException;
}