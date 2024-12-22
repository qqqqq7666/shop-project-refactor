package com.example.shop_project.category.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryCustomException extends RuntimeException {
    CategoryErrorCode categoryErrorCode;
}
