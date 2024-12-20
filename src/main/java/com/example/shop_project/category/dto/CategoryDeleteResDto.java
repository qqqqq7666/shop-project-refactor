package com.example.shop_project.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryDeleteResDto {
    private Long subCategoryId;
    private Long mainCategoryId;
    private boolean existMainCategory;
}
