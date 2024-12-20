package com.example.shop_project.category.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CategoryCustomExceptionHandler {
    @ExceptionHandler(CategoryCustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCategoryCustomException(CategoryCustomException e) {
        return ErrorResponseEntity.toResponseEntity(e.getCategoryErrorCode());
    }
}
