package com.yummy.beckend.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yummy.beckend.dao.UserDAO;
import com.yummy.beckend.dto.UserDto;
import com.yummy.beckend.exception.DuplicateEmailException;
import com.yummy.beckend.exception.DuplicateNameException;
import com.yummy.beckend.exception.InvalidCredentialsException;
import com.yummy.beckend.exception.NotFoundUserException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCryptPasswordEncoder Bean 주입

    /**
     * C-1. 회원 등록 (Regist) 로직 구현
     */
    @Override
    @Transactional(rollbackFor = { SQLException.class, DuplicateEmailException.class, DuplicateNameException.class })
    public void regist(UserDto userDto) throws DuplicateEmailException, DuplicateNameException, SQLException {
        
        // 1. 아이디(이메일) 중복 확인
        if (checkEmail(userDto.getEmail()) > 0) {
            throw new DuplicateEmailException("이미 사용 중인 이메일(아이디)입니다.");
        }

        // 2. 닉네임 중복 확인
        if (checkName(userDto.getName()) > 0) {
            throw new DuplicateNameException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 암호화
        String encryptedPwd = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encryptedPwd);

        // 4. DB에 사용자 정보 삽입
        userDAO.insertUser(userDto);
    }

    /**
     * C-2. 로그인 로직 구현
     * (이전 프로젝트의 login 메서드 참고)
     */
    @Override
    public UserDto login(String email, String password) throws NotFoundUserException, InvalidCredentialsException, SQLException {
        
        // 1. 이메일로 사용자 정보 조회
        UserDto member = userDAO.findByEmail(email);

        // 2. 사용자 없음 처리
        if (member == null) {
            throw new NotFoundUserException("아이디(이메일) 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. 비밀번호 검증 (평문 비밀번호와 저장된 암호화 비밀번호 비교)
        // passwordEncoder.matches(평문, 암호화된 비밀번호)
        if (!passwordEncoder.matches(password, member.getPassword())) {
             throw new InvalidCredentialsException("아이디(이메일) 또는 비밀번호가 올바르지 않습니다.");
        }
        
        // *참고: 로그인 성공 후, 세션 처리는 Controller에서 담당합니다.

        return member;
    }

    /**
     * 중복 확인 보조 메서드
     */
    @Override
    public int checkEmail(String email) throws SQLException {
        return userDAO.countByEmail(email);
    }

    @Override
    public int checkName(String name) throws SQLException {
        return userDAO.countByName(name);
    }
}