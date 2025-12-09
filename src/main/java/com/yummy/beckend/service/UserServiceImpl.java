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
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void regist(UserDto userDto) throws DuplicateEmailException, DuplicateNameException, SQLException {
        
        // 1. 이메일 중복 검사
        if (isEmailExists(userDto.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        // 2. 닉네임 중복 검사
        if (isNicknameExists(userDto.getName())) {
            throw new DuplicateNameException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 암호화
        String encryptedPwd = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encryptedPwd);

        // 4. 저장
        userDAO.insertUser(userDto);
    }

    @Override
    public UserDto login(String email, String password) throws NotFoundUserException, InvalidCredentialsException, SQLException {
        UserDto member = userDAO.findByEmail(email);

        if (member == null) {
            throw new NotFoundUserException("계정을 찾을 수 없습니다.");
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
             throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        
        return member;
    }

    // 컨트롤러에서 사용하기 위해 boolean 타입으로 반환
    @Override
    public boolean isEmailExists(String email) throws SQLException {
        return userDAO.countByEmail(email) > 0;
    }

    @Override
    public boolean isNicknameExists(String name) throws SQLException {
        return userDAO.countByName(name) > 0;
    }
}