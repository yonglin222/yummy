package com.yummy.beckend.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yummy.beckend.dto.FridgeDto;
import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.exception.UnauthorizedAccessException;
import com.yummy.beckend.service.FridgeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "2. 냉장고 관리", description = "재료 등록, 수정, 삭제, 조회 (AJAX)")
@Controller
@RequestMapping("/fridge")
public class FridgeController {

    @Autowired
    private FridgeService fridgeService;
    
    private Long getUserId(HttpSession session) throws UnauthorizedAccessException {
        UserDto user = (UserDto) session.getAttribute("loginUser");
        if (user == null || user.getId() == null) {
            throw new UnauthorizedAccessException("로그인된 사용자 정보가 없습니다.");
        }
        return user.getId();
    }

    @Operation(summary = "냉장고 목록 페이지", description = "냉장고 관리 HTML 페이지를 반환합니다.")
    @GetMapping("/list")
    public String fridgeList(@Parameter(hidden = true) HttpSession session, 
                             @Parameter(hidden = true) Model model) {
        try {
            Long userId = getUserId(session); 
            model.addAttribute("fridgeDto", new FridgeDto());
            return "fridge/list"; 
        } catch (UnauthorizedAccessException e) {
            return "redirect:/user/loginForm";
        }
    }
    
    @Operation(summary = "재료 목록 조회 (AJAX)", description = "로그인한 사용자의 모든 재료 목록을 JSON으로 반환합니다.")
    @GetMapping("/data")
    @ResponseBody
    public List<FridgeDto> getFridgeData(@Parameter(hidden = true) HttpSession session) throws SQLException, UnauthorizedAccessException {
        Long userId = getUserId(session);
        return fridgeService.getIngredientsByUserId(userId);
    }

    @Operation(summary = "재료 등록 (AJAX)", description = "새로운 재료를 등록합니다.")
    @PostMapping("/registAjax")
    @ResponseBody
    public Map<String, Object> registIngredientAjax(@Valid FridgeDto fridgeDto,
                                                    @Parameter(hidden = true) BindingResult result,
                                                    @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserId(session);
            fridgeDto.setUserId(userId);

            if (result.hasErrors()) {
                response.put("status", "FAIL");
                response.put("message", result.getFieldError().getDefaultMessage()); 
                response.put("errors", result.getAllErrors());
                return response;
            }
            
            fridgeService.registIngredient(fridgeDto);
            
            response.put("status", "OK");
            response.put("message", "재료 '" + fridgeDto.getIngredient() + "'가 등록되었습니다.");
            
        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", "로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "데이터베이스 오류가 발생했습니다.");
        }
        
        return response;
    }

    @Operation(summary = "재료 삭제 (AJAX)", description = "특정 ID의 재료를 삭제합니다.")
    @PostMapping("/removeAjax")
    @ResponseBody
    public Map<String, Object> removeIngredientAjax(@Parameter(description = "삭제할 재료 ID") @RequestParam("id") Long ingredientId,
                                                    @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserId(session);
            fridgeService.removeIngredient(ingredientId, userId);
            response.put("status", "OK");
            response.put("message", "재료가 삭제되었습니다.");
            
        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "데이터베이스 오류가 발생했습니다.");
        }
        
        return response;
    }

    @Operation(summary = "재료 수정 (AJAX)", description = "재료 정보를 수정합니다.")
    @PostMapping("/modifyAjax") 
    @ResponseBody
    public Map<String, Object> modifyIngredientAjax(@Valid FridgeDto fridgeDto,
                                                    @Parameter(hidden = true) BindingResult result,
                                                    @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserId(session);
            fridgeDto.setUserId(userId);

            if (result.hasErrors()) {
                response.put("status", "FAIL");
                response.put("message", result.getFieldError().getDefaultMessage());
                return response;
            }

            fridgeService.modifyIngredient(fridgeDto);
            response.put("status", "OK");
            response.put("message", "재료 정보가 수정되었습니다.");

        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", "수정 권한이 없습니다: " + e.getMessage()); 
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "재료 수정 중 데이터베이스 오류가 발생했습니다.");
        }
        
        return response;
    }

    @Operation(summary = "재료 다중 삭제 (AJAX)", description = "선택한 여러 재료를 한 번에 삭제합니다.")
    @PostMapping("/removeMultipleAjax")
    @ResponseBody
    public Map<String, Object> removeMultipleIngredients(@Parameter(description = "삭제할 재료 ID 리스트") @RequestParam(value = "ids[]") List<Long> ids,
                                                         @Parameter(hidden = true) HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(session);
            
            if (ids == null || ids.isEmpty()) {
                response.put("status", "FAIL");
                response.put("message", "삭제할 재료가 선택되지 않았습니다.");
                return response;
            }

            fridgeService.removeIngredients(ids, userId);
            response.put("status", "OK");
            response.put("message", ids.size() + "개의 재료가 삭제되었습니다.");
            
        } catch (UnauthorizedAccessException e) {
            response.put("status", "ERROR");
            response.put("message", "로그인 세션이 만료되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "ERROR");
            response.put("message", "삭제 중 오류가 발생했습니다.");
        }
        return response;
    }
}