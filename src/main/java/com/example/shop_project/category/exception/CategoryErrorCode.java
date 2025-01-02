package com.example.shop_project.category.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CategoryErrorCode {
    // 409 Conflict
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "중복된 카테고리명입니다."),
    CONFLICT_WITH_PRODUCTS(HttpStatus.CONFLICT, "해당 카테고리에 상품이 존재하여 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
