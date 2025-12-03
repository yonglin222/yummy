package com.yummy.beckend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RecipeDto {

    private Long recipeId;
    private String name;
    private Integer serving;
    private Integer time;
    private String ingredient;
    private String spicyIngredient;
    private String method; 
    
    private List<String> methodSteps; 
    
    private List<String> typeCategories;
    private List<String> methodCategories;
    
    private Boolean isFavorite = false;
}