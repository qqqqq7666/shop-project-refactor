package com.example.shop_project.category.service;

import com.example.shop_project.category.dto.CategoryCreateReqDto;
import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.dto.CategoryUpdateReqDto;
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
    public CategoryResDto createCategory(CategoryCreateReqDto categoryCreateReqDto) {
        Category mainCategory = categoryRepository.findByCategoryName(categoryCreateReqDto.getMainCategoryName());
        // 메인 카테고리 생성
        if(mainCategory == null) {
            mainCategory = new Category();
            mainCategory.setCategoryName(categoryCreateReqDto.getMainCategoryName());
            mainCategory.setDepth(0);
            categoryRepository.save(mainCategory);
        }

        //Long mainCategoryId = mainCategory.getCategoryId();

        // 서브 카테고리 생성
        Category newSubCategory = new Category();
        newSubCategory.setCategoryName(categoryCreateReqDto.getSubCategoryName());
        newSubCategory.setDepth(mainCategory.getDepth() + 1); // 서브 카테고리의 깊이는 0 + 1
        newSubCategory.setParentCategory(mainCategory);
        mainCategory.getSubCategories().add(newSubCategory); // 부모-자식 동기화
        categoryRepository.save(newSubCategory);

        return toCategoryResDto(mainCategory);
    }

    // 카테고리 수정
    @Transactional
    public CategoryResDto updateCategory(CategoryUpdateReqDto categoryUpdateReqDto) {
        log.info("카테고리 업데이트 요청 - ID: {}, 새로운 이름: {}", categoryUpdateReqDto.getCategoryId(), categoryUpdateReqDto.getCategoryName());
        Category category = categoryRepository.findByCategoryId(categoryUpdateReqDto.getCategoryId());
        String updateCategoryName = categoryUpdateReqDto.getCategoryName();

        // 카테고리명 중복되면 변경 X
        boolean existByCategoryName = categoryRepository.existsByCategoryName(updateCategoryName);
        if(existByCategoryName) {
            return null;
        }

        // 카테고리명 업데이트
        category.setCategoryName(updateCategoryName);
        categoryRepository.save(category);

        return toCategoryResDto(category);
    }

    // 카테고리 삭제
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        log.info("##############service categoryId: {}", categoryId);
        Category category = categoryRepository.findByCategoryId(categoryId);
        String categoryName = category.getCategoryName();

        log.info("##############service categoryName: {}", categoryName);

        Category parentCategory = category.getParentCategory();
        parentCategory.getSubCategories().remove(category); // 연관관계 삭제

        if(category != null) {
            // productList 비었는지 확인 후 비었으면
            categoryRepository.delete(category); // 서브 카테고리 삭제

            // 메인 카테고리에 서브 카테고리 없으면 메인 카테고리 삭제
            if(parentCategory != null) {
                List<Category> subCategories = parentCategory.getSubCategories();
                if(subCategories.isEmpty()) {
                    categoryRepository.delete(parentCategory); // 메인 카테고리 삭제
                }
            }

            Category deleteCategory = categoryRepository.findByCategoryId(categoryId);
            if(deleteCategory != null) {
                return false;
            }
            return true;
        }
        return false;
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
