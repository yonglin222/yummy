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
import org.springframework.web.bind.annotation.ResponseBody;
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

@Tag(name = "1. 회원 관리", description = "회원가입, 로그인, 중복 체크 API")
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "회원가입 페이지 이동", description = "회원가입 HTML 폼을 반환합니다.")
    @GetMapping("/registForm")
    public String registForm(@ModelAttribute("userDto") UserDto userDto) {
        return "user/signup"; 
    }

    @Operation(summary = "닉네임 중복 확인 (AJAX)", description = "사용 가능한 닉네임인지 확인합니다. (return: OK / DUPLICATED)")
    @GetMapping("/nameCheck")
    @ResponseBody
    public String nameCheck(@Parameter(description = "확인할 닉네임") String name) throws SQLException {
        int count = userService.checkName(name);
        if (count > 0) {
            return "DUPLICATED"; 
        }
        return "OK"; 
    }

    @Operation(summary = "이메일 중복 확인 (AJAX)", description = "사용 가능한 이메일인지 확인합니다. (return: OK / DUPLICATED)")
    @GetMapping("/emailCheck")
    @ResponseBody
    public String emailCheck(@Parameter(description = "확인할 이메일") String email) throws SQLException {
        int count = userService.checkEmail(email);
        if (count > 0) {
            return "DUPLICATED";
        }
        return "OK";
    }

    @Operation(summary = "회원가입 요청", description = "회원 정보를 등록하고 로그인 페이지로 리다이렉트합니다.")
    @PostMapping("/regist")
    public String regist(@Valid @ModelAttribute("userDto") UserDto userDto, 
                         @Parameter(hidden = true) BindingResult result,
                         @Parameter(hidden = true) RedirectAttributes rttr) throws Exception {

        if (result.hasErrors()) {
            return "user/signup";
        }

        try {
            userService.regist(userDto);
            rttr.addFlashAttribute("message", "환영합니다! 회원가입이 성공적으로 완료되었습니다. 로그인해주세요.");
            return "redirect:/user/loginForm";

        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "duplicate.email", e.getMessage());
            return "user/signup";
        } catch (DuplicateNameException e) {
            result.rejectValue("name", "duplicate.name", e.getMessage());
            return "user/signup";
        } catch (SQLException e) {
            e.printStackTrace();
            rttr.addFlashAttribute("message", "서버 오류가 발생했습니다. 관리자에게 문의하세요.");
            return "redirect:/user/registForm";
        }
    }

    @Operation(summary = "로그인 페이지 이동", description = "로그인 HTML 폼을 반환합니다.")
    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("userDto") UserDto userDto) {
        return "user/login";
    }

    @Operation(summary = "로그인 요청", description = "로그인을 처리하고 세션을 생성합니다.")
    @PostMapping("/login")
    public String login(@Parameter(description = "이메일") String email, 
                        @Parameter(description = "비밀번호") String password, 
                        @Parameter(hidden = true) HttpSession session, 
                        @Parameter(hidden = true) RedirectAttributes rttr) throws SQLException {
        
        try {
            UserDto loginUser = userService.login(email, password);
            session.setAttribute("loginUser", loginUser);

            String prevPage = (String) session.getAttribute("prevPage");
            session.removeAttribute("prevPage");

            rttr.addFlashAttribute("message", loginUser.getName() + "님, 환영합니다!");
            
            if (prevPage != null && !prevPage.isEmpty()) {
                return "redirect:" + prevPage; 
            } else {
                return "redirect:/"; 
            }

        } catch (NotFoundUserException | InvalidCredentialsException e) {
            rttr.addFlashAttribute("message", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/user/loginForm";
        }
    }

    @Operation(summary = "로그아웃", description = "세션을 만료시키고 메인 페이지로 이동합니다.")
    @GetMapping("/logout")
    public String logout(@Parameter(hidden = true) HttpSession session, 
                         @Parameter(hidden = true) RedirectAttributes rttr) {
        session.invalidate();
        rttr.addFlashAttribute("message", "로그아웃 되었습니다.");
        return "redirect:/";
    }
}