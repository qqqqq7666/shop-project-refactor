package com.example.shop_project.category.service;

import com.example.shop_project.category.dto.CategoryReqDto;
import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.entity.Category;
import com.example.shop_project.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 전체 카테고리 조회
    public List<CategoryResDto> getAllCategories() {
        List<Category> mainCategories = categoryRepository.findByDepth(0); // 메인 카테고리

        List<CategoryResDto> categoryResDtos = mainCategories.stream()
                .map(this::toCategoryResDto)
                .collect(Collectors.toList());

        return categoryResDtos;
    }

    // 카테고리 생성
    @Transactional
    public CategoryResDto createCategory(CategoryReqDto categoryReqDto) {
        Category mainCategory = categoryRepository.findByCategoryName(categoryReqDto.getMainCategoryName());
        // 메인 카테고리 생성
        if(mainCategory == null) {
            mainCategory = new Category();
            mainCategory.setCategoryName(categoryReqDto.getMainCategoryName());
            mainCategory.setDepth(0);
            categoryRepository.save(mainCategory);
        }

        //Long mainCategoryId = mainCategory.getCategoryId();

        // 서브 카테고리 생성
        Category newSubCategory = new Category();
        newSubCategory.setCategoryName(categoryReqDto.getSubCategoryName());
        newSubCategory.setDepth(mainCategory.getDepth() + 1); // 서브 카테고리의 깊이는 0 + 1
        newSubCategory.setParentCategory(mainCategory);
        mainCategory.getSubCategories().add(newSubCategory); // 부모-자식 동기화
        categoryRepository.save(newSubCategory);

        return toCategoryResDto(mainCategory);
    }

    // Entity -> CategoryResDto 메서드
    private CategoryResDto toCategoryResDto(Category category) {
        List<CategoryResDto> subCategoryDtos = Optional.ofNullable(category.getSubCategories())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toCategoryResDto)
                .collect(Collectors.toList());

        return new CategoryResDto(category.getCategoryId(), category.getCategoryName(), category.getDepth(), subCategoryDtos);
    }
}
