package com.yummy.beckend.service;

import com.yummy.beckend.dao.CategoryDAO;
import com.yummy.beckend.dto.CategoryDto;
import com.yummy.beckend.dto.RecipeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public List<CategoryDto> getAllTypeCategories() throws SQLException {
        return categoryDAO.findAllTypeCategories();
    }

    @Override
    public List<CategoryDto> getAllMethodCategories() throws SQLException {
        return categoryDAO.findAllMethodCategories();
    }
    
    // @Override
    // public List<RecipeDto> getRecipesByTypeAndMethod(Long typeCatId, Long methodCatId) throws SQLException {
    //     return categoryDAO.findRecipesByTypeAndMethod(typeCatId, methodCatId);
    // }
}