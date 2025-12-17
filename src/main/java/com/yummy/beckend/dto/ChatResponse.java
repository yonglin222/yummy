package com.yummy.beckend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter @Setter
@AllArgsConstructor
public class ChatResponse {
    private String response; // AI의 답변 텍스트
    private RecipeDto recommendedRecipe; // AI가 추천한 레시피 정보
}