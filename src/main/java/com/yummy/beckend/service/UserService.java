package com.yummy.beckend.service;

import java.sql.SQLException;
import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.exception.DuplicateEmailException;
import com.yummy.beckend.exception.DuplicateNameException;
import com.yummy.beckend.exception.InvalidCredentialsException;
import com.yummy.beckend.exception.NotFoundUserException;

public interface UserService {

    // 회원 등록
    void regist(UserDto userDto) throws DuplicateEmailException, DuplicateNameException, SQLException;

    // 로그인
    UserDto login(String email, String password) throws NotFoundUserException, InvalidCredentialsException, SQLException;

    // 이메일 중복 확인
    boolean isEmailExists(String email) throws SQLException;

    // 닉네임 중복 확인
    boolean isNicknameExists(String name) throws SQLException;
}