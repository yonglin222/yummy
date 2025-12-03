package com.yummy.beckend.exception;

public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(String message) {
        super("로그인 시 사용자를 찾을 수 없는 오류 처리.");
    }
}