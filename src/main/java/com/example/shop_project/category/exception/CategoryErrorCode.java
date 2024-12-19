package com.example.shop_project.category.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CategoryErrorCode {
    // 409 Conflict
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "중복된 카테고리명입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
