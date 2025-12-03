package com.yummy.beckend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryDto {
    private Long id;   // TYPE_ID 또는 METHOD_ID
    private String name; // TYPE_NAME 또는 METHOD_NAME
    private String type; // 카테고리 유형 ('type' 또는 'method')
}