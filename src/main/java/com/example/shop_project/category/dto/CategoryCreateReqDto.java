package com.example.shop_project.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryCreateReqDto {
    @NotBlank(message = "Category name is required.")
    @Size(min = 2, max = 15, message = "Category name must be between 2 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z가-힣\\s/]+$", message = "Category name must contain only letters, spaces, and '/'.")
    private String mainCategoryName;

    @NotBlank(message = "Category name is required.")
    @Size(min = 2, max = 15, message = "Category name must be between 2 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z가-힣\\s/]+$", message = "Category name must contain only letters, spaces, and '/'.")
    private String subCategoryName;

    private boolean creatingMainCategory; // 메인 카테고리 생성 중인지(true: 메인 카테고리 생성, false: 서브 카테고리 생성)

}
