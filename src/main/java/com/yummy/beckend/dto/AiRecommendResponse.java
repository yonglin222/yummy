package com.yummy.beckend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AiRecommendResponse {

    @JsonProperty("user_id")
    private String userId;

    private String query;

    @JsonProperty("recipe_id")
    private Long recipeId;

    private String answer;

    private Map<String, Object> tags;
}