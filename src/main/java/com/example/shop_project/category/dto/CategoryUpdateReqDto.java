package com.example.shop_project.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryUpdateReqDto {
    private Long categoryId;

    @NotBlank(message = "Category name is required.")
    @Size(min = 1, max = 15, message = "Category name must be between 2 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z가-힣\\s/]+$", message = "Category name must contain only letters, spaces, and '/'.")
    private String categoryName;
}
