package com.example.shop_project.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryResDto {
    private Long categoryId;
    private String categoryName;
    private int depth;
    private int productCount;
    private List<CategoryResDto> subCategories = new ArrayList<>();
}
