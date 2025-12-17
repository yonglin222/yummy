package com.yummy.beckend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.yummy.beckend.dto.ChatRequest;
import com.yummy.beckend.dto.ChatResponse;
import com.yummy.beckend.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "3. AI 채팅", description = "AI 레시피 추천 채팅")
@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(summary = "채팅방 입장", description = "채팅 화면 HTML을 반환")
    @GetMapping("")
    public String chatPage() {
        return "chat/room";
    }

    @Operation(summary = "메인화면에서 채팅 시작 (SSR)", description = "메인화면의 질문을 가지고 채팅방으로 이동")
    @PostMapping("/start")
    public String startChat(
            @Parameter(description = "사용자 질문")
            @RequestParam("message") String message,
            Model model
    ) {

        // AI + 레시피 추천 한 번에 처리
        ChatResponse response =
                chatService.chat(message, "guest");

        model.addAttribute("startUserMsg", message);
        model.addAttribute("startAiMsg", response.getResponse());
        model.addAttribute("startRecipe", response.getRecommendedRecipe());

        return "chat/room";
    }

    @Operation(summary = "메시지 전송 (AJAX)", description = "사용자 메시지를 보내고 AI 응답을 받음")
    @PostMapping("/send")
    @ResponseBody
    public ChatResponse sendMessage(
            @RequestBody ChatRequest request
    ) {
        return chatService.chat(
                request.getMessage(),
                "guest"
        );
    }
}