package com.yummy.beckend.service;

import com.yummy.beckend.dto.AiRecommendResponse;

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

    /**
     * 🔥 AI 추천 요청
     * @param query 사용자가 입력한 문장
     * @param userId 로그인 회원ID or "guest"
     */
    public AiRecommendResponse recommend(String query, String userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/recommend")
                        .queryParam("query", query)
                        .queryParam("user_id", userId)
                        .build())
                .retrieve()
                .bodyToMono(AiRecommendResponse.class)
                .block();
    }

}
