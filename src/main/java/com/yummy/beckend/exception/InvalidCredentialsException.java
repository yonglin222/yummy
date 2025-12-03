package com.yummy.beckend.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super("로그인 시 비밀번호 불일치 오류 처리.");
    }
}