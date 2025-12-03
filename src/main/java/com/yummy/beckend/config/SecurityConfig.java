package com.yummy.beckend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 암호화 객체(PasswordEncoder)를 Bean으로 등록합니다.
     * Spring Security를 사용하지 않더라도 암호화 기능만 별도로 사용할 수 있습니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}