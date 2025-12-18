package com.yummy.beckend.service;

import com.yummy.beckend.dto.AiRecommendResponse;
import com.yummy.beckend.dto.FridgeRecommendRequest;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiService {

    private final WebClient webClient;

    public AiService(
    @Value("${fastapi.ai.url:http://192.168.0.7:8000}") String baseUrl
) {
    this.webClient = WebClient.create(
        Objects.requireNonNull(baseUrl)
    );
}

    public AiRecommendResponse recommendChat(String query, String userId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/recommend/chat")
                .queryParam("query", query)
                .queryParam("user_id", userId)
                .build())
            .retrieve()
            .bodyToMono(AiRecommendResponse.class)
            .block();
    }

    public AiRecommendResponse recommendFridge(
        List<String> ingredients,
        String userId
    ) {
        FridgeRecommendRequest body =
            new FridgeRecommendRequest(ingredients, userId);

        return webClient.post()
            .uri("/api/recommend/fridge")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(AiRecommendResponse.class)
            .block();
    }
}

