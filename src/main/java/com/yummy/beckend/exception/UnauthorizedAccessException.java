package com.yummy.beckend.exception;

// 권한이 없는 접근(예: 타인 데이터 접근, 비로그인 상태) 시 발생하는 예외
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}