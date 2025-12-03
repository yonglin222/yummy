package com.yummy.beckend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "0. 메인", description = "메인 홈페이지")
@Controller
public class HomeController {

    @Operation(summary = "메인 화면", description = "야미 서비스의 메인 화면으로 이동합니다.")
    @GetMapping("/")
    public String home() {
        return "main"; 
    }
}