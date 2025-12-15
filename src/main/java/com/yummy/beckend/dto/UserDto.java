package com.yummy.beckend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

    private Long id;

    // A-1. 닉네임: 한글/영어 2장 이상 10글자 이하 
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해야 합니다.")
    private String name; 

    // A-1. 아이디(이메일)
    @NotBlank(message = "아이디(이메일)는 필수 입력 항목입니다.")
    @Email(message = "아이디는 이메일 형식이어야 합니다.")
    private String email; 

    // A-1. 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력해야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$",
             message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password; 
}