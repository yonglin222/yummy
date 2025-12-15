package com.yummy.beckend.dao;

import java.sql.SQLException;
import com.yummy.beckend.dto.UserDto;

public interface UserDAO {

    // 회원 등록
    void insertUser(UserDto userDto) throws SQLException;

    // 닉네임 중복 확인
    int countByName(String name) throws SQLException;

    // 아이디(이메일) 중복 확인
    int countByEmail(String email) throws SQLException;

    // 사용자 정보 조회
    UserDto findByEmail(String email) throws SQLException;  
}