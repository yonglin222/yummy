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
    // 조리 순서
    private List<String> methodSteps; 
    // 카테고리
    private List<String> typeCategories;
    private List<String> methodCategories;
    // 즐겨찾기 여부
    private Boolean isFavorite = false;
}