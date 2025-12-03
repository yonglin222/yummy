package com.yummy.beckend.dao;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.yummy.beckend.dto.UserDto;

@Repository // DAO 역할을 하는 Bean임을 명시
public class UserDAOImpl implements UserDAO {

    // MyBatis 설정에 따라 주입 (SqlSessionFactoryBean이 등록되어 있어야 함)
    @Autowired
    private SqlSession sqlSession;
    
    // MyBatis XML의 namespace를 요청하신대로 "User-Mapper"로 지정합니다.
    private static final String NAMESPACE = "User-Mapper";

    @Override
    public void insertUser(UserDto userDto) throws SQLException {
        // "User-Mapper.insertUser"를 호출합니다.
        sqlSession.insert(NAMESPACE + ".insertUser", userDto);
    }

    @Override
    public int countByName(String name) throws SQLException {
        // "User-Mapper.countByName"을 호출합니다.
        return sqlSession.selectOne(NAMESPACE + ".countByName", name);
    }

    @Override
    public int countByEmail(String email) throws SQLException {
        // "User-Mapper.countByEmail"을 호출합니다.
        return sqlSession.selectOne(NAMESPACE + ".countByEmail", email);
    }

    @Override
    public UserDto findByEmail(String email) throws SQLException {
        // "User-Mapper.findByEmail"을 호출합니다.
        return sqlSession.selectOne(NAMESPACE + ".findByEmail", email);
    }
    
    @Override
    public void updateUser(UserDto userDto) throws SQLException {
        // 이 기능은 향후 사용자를 수정할 때 필요합니다. (XML에 updateMember와 유사한 SQL 추가 예정)
        // 현재는 구현을 위해 메서드만 정의하고, 실제 SQL은 추후 작성하겠습니다.
        // sqlSession.update(NAMESPACE + ".updateUser", userDto);
    }

    // 탈퇴 기능은 현재 USERS 테이블에 status가 없어 생략합니다.
}