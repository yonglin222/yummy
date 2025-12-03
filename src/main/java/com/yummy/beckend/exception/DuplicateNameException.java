package com.yummy.beckend.exception;

public class DuplicateNameException extends RuntimeException {
    public DuplicateNameException(String message) {
        super("회원가입 시 닉네임 중복 오류 처리.");
    }
}