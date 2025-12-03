package com.yummy.beckend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatRequest {
    private String message; // 사용자가 보낸 메시지
}