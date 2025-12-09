package com.yummy.beckend.controller;

import com.yummy.beckend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "1-1. 회원 API", description = "회원가입 중복 체크 등 JSON 응답 API")
@RestController
@RequestMapping("/api")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Operation(summary = "닉네임 중복 확인", description = "JS: { available: true/false } 형태로 반환")
    @PostMapping("/check-nickname")
    public Map<String, Boolean> checkNickname(@RequestParam("nickname") String nickname) throws SQLException {
        boolean exists = userService.isNicknameExists(nickname);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !exists); // 존재하지 않아야 사용 가능
        return response;
    }

    @Operation(summary = "이메일 중복 확인", description = "JS: true/false 반환")
    @PostMapping("/check-email")
    public boolean checkEmail(@RequestParam("email") String email) throws SQLException {
        // 존재하면 true 리턴 -> 프론트에서 true면 "중복"으로 처리
        return userService.isEmailExists(email);
    }
}