package com.yummy.beckend.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.exception.DuplicateEmailException;
import com.yummy.beckend.exception.DuplicateNameException;
import com.yummy.beckend.exception.InvalidCredentialsException;
import com.yummy.beckend.exception.NotFoundUserException;
import com.yummy.beckend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.sql.SQLException;

@Tag(name = "1. 회원 페이지", description = "회원가입/로그인 화면 이동 및 폼 전송")
@Controller 
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 회원가입 관련
    @Operation(summary = "회원가입 페이지 이동")
    @GetMapping("/registForm")
    public String registForm(@ModelAttribute("userDto") UserDto userDto) {
        return "user/signup"; 
    }

    @Operation(summary = "회원가입 요청 처리")
    @PostMapping("/regist")
    public String regist(@Valid @ModelAttribute("userDto") UserDto userDto, 
                         BindingResult result,
                         RedirectAttributes rttr) {

        if (result.hasErrors()) {
            return "user/signup";
        }

        try {
            userService.regist(userDto);
            return "redirect:/user/loginForm";

        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "duplicate", e.getMessage());
            return "user/signup";
        } catch (DuplicateNameException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            return "user/signup";
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("message", "서버 오류가 발생했습니다.");
            return "redirect:/user/registForm";
        }
    }

    // 2. 로그인 관련

    @Operation(summary = "로그인 페이지 이동")
    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("userDto") UserDto userDto) {
        return "user/login";
    }

    @Operation(summary = "로그인 요청 처리")
    @PostMapping("/login")
    // 이메일, 비밀번호, 세션, 리다이렉트 속성
    public String login(@Parameter(description = "이메일") String email, 
                        @Parameter(description = "비밀번호") String password, 
                        HttpSession session, 
                        RedirectAttributes rttr) throws SQLException {
        
        try {
            UserDto loginUser = userService.login(email, password);
            session.setAttribute("loginUser", loginUser);
            
            String prevPage = (String) session.getAttribute("prevPage");
            session.removeAttribute("prevPage");
            // 이전 페이지가 있으면 그쪽으로, 없으면 메인으로
            if (prevPage != null && !prevPage.isEmpty()) {
                return "redirect:" + prevPage; 
            } else {
                return "redirect:/"; 
            }
            
        } 
        // 이메일 또는 비밀번호 오류 처리
        catch (NotFoundUserException | InvalidCredentialsException e) {
            rttr.addFlashAttribute("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/user/loginForm";
        }
    }

    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes rttr) {
        session.invalidate();
        // rttr.addFlashAttribute("message", "로그아웃 되었습니다.");
        return "redirect:/";
    }
}