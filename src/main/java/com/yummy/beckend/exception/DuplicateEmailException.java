package com.yummy.beckend.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super("회원가입 시 이메일 중복 오류 처리.");
    }
}