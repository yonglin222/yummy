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

    /**
     * 1. 채팅방 입장 (메인에서 이동하거나 직접 접속 시)
     * URL: GET /chat/room?msg=검색어
     */
    @Operation(summary = "채팅방 입장", description = "채팅 화면 HTML을 반환. msg 파라미터가 있으면 초기 질문으로 사용.")
    @GetMapping("/room") // intro.html의 location.href 주소와 맞춤
    public String chatPage(
            @RequestParam(value = "msg", required = false) String msg, 
            Model model
    ) {
        // 여기서 AI를 호출하지 않습니다! 
        // 그냥 메시지만 모델에 담아서 페이지를 즉시 띄웁니다.
        model.addAttribute("startMsg", msg);
        return "chat/room";
    }

    /**
     * 2. 메시지 전송 (비동기 AJAX 전용)
     * URL: POST /chat/send
     * 채팅방 안에서 입력하거나, 페이지 로드 후 JS가 자동으로 호출함
     */
    @Operation(summary = "메시지 전송 (AJAX)", description = "사용자 메시지를 보내고 AI 응답을 받음")
    @PostMapping("/send")
    @ResponseBody
    public ChatResponse sendMessage(
            @RequestBody ChatRequest request
    ) {
        // 실제 AI 연산은 여기서만 일어납니다.
        return chatService.chat(
                request.getMessage(),
                "guest"
        );
    }

    /**
     * (참고) 기존에 있던 /start (POST) 방식은 이제 사용하지 않으므로 
     * 삭제하거나 그대로 두셔도 됩니다. 위 /room 방식이 더 빠릅니다.
     */
    @Operation(summary = "기존 SSR 방식 (미사용 권장)", description = "메인화면의 질문을 가지고 채팅방으로 이동")
    @PostMapping("/start")
    public String startChat(
            @RequestParam("message") String message,
            Model model
    ) {
        // 이 방식은 AI 응답이 올 때까지 페이지가 안 넘어가서 느립니다.
        ChatResponse response = chatService.chat(message, "guest");
        model.addAttribute("startUserMsg", message);
        model.addAttribute("startAiMsg", response.getResponse());
        model.addAttribute("startRecipe", response.getRecommendedRecipe());
        return "chat/room";
    }
}