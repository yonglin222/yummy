package com.yummy.beckend.service;

import java.sql.SQLException;
// 필요한 커스텀 예외 클래스들은 이전에 스켈레톤으로 추가되었다고 가정합니다.
import com.yummy.beckend.exception.DuplicateEmailException;
import com.yummy.beckend.exception.DuplicateNameException;
import com.yummy.beckend.exception.InvalidCredentialsException;
import com.yummy.beckend.exception.NotFoundUserException;
import com.yummy.beckend.dto.UserDto;

public interface UserService {

    // C-1. 회원 등록 (Regist)
    void regist(UserDto userDto) throws DuplicateEmailException, DuplicateNameException, SQLException;

    // C-2. 로그인
    UserDto login(String email, String password) throws NotFoundUserException, InvalidCredentialsException, SQLException;

    // 아이디(이메일) 중복 확인
    int checkEmail(String email) throws SQLException;

    // 닉네임 중복 확인
    int checkName(String name) throws SQLException;
    
    // ... 기타 수정/탈퇴 메서드 (추후 추가)
}
