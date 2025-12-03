package com.yummy.beckend.dao;

import java.sql.SQLException;
import com.yummy.beckend.dto.UserDto;

// MemberDAO와 유사하게 SQLException을 던지도록 정의합니다.
public interface UserDAO {

    // C-1. 회원 등록
    void insertUser(UserDto userDto) throws SQLException;

    // C-1. 닉네임 중복 확인 (0 또는 1 반환)
    int countByName(String name) throws SQLException;

    // C-1. 아이디(이메일) 중복 확인 (0 또는 1 반환)
    int countByEmail(String email) throws SQLException;

    // C-2. 로그인 및 정보 수정을 위한 사용자 정보 조회
    UserDto findByEmail(String email) throws SQLException;
    
    // 추가 기능: 회원 정보 수정 (update)
    void updateUser(UserDto userDto) throws SQLException;

    // 추가 기능: 회원 탈퇴 (delete, 또는 status 변경)
    // 현재 USERS 테이블은 status 칼럼이 없으므로, 향후 기능 확장 시 고려
}