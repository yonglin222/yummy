package com.yummy.beckend.dao;

import java.sql.SQLException;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.yummy.beckend.dto.UserDto;

@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    private SqlSession sqlSession;
    
    private static final String NAMESPACE = "User-Mapper";

    @Override
    public void insertUser(UserDto userDto) throws SQLException {
        sqlSession.insert(NAMESPACE + ".insertUser", userDto);
    }

    @Override
    public int countByName(String name) throws SQLException {
        return sqlSession.selectOne(NAMESPACE + ".countByName", name);
    }

    @Override
    public int countByEmail(String email) throws SQLException {
        return sqlSession.selectOne(NAMESPACE + ".countByEmail", email);
    }

    @Override
    public UserDto findByEmail(String email) throws SQLException {
        return sqlSession.selectOne(NAMESPACE + ".findByEmail", email);
    }
}