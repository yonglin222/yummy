package com.yummy.beckend.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FridgeDto {

    private Long id;
    private Long userId;

    @NotBlank(message = "재료명은 필수 입력 항목입니다.")
    private String ingredient;

    @NotBlank(message = "보관 구분은 필수입니다.")
    @Pattern(regexp = "^(냉장|냉동|상온)$", message = "보관 구분은 '냉장', '냉동', '상온' 중 하나여야 합니다.")
    private String category;

    private LocalDate expirationDate; 

    private Long dday; 
}