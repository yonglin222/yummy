package com.yummy.beckend.dto;

import java.util.List;

public record FridgeRecommendRequest(
    List<String> ingredients,
    String user_id
) {

}
