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

import java.util.*;
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

    // 메인 카테고리 생성
    @Transactional
    public CategoryResDto createMainCategory(CategoryCreateReqDto categoryCreateReqDto) {
        log.debug("카테고리 추가(service) - 메인 카테고리 추가 메서드 실행({})", categoryCreateReqDto.isCreatingMainCategory());

        // 메인 카테고리명 중복 검사
        List<Category> mainCategories = categoryRepository.findByDepth(0); // 메인 카테고리
        if(isCategoryNameDuplicate(categoryCreateReqDto.getMainCategoryName(), mainCategories)){
            return null;
        }

        // 메인 카테고리 생성
        Category newMainCategory = new Category();
        newMainCategory.setCategoryName(categoryCreateReqDto.getMainCategoryName());
        newMainCategory.setDepth(0);
        categoryRepository.save(newMainCategory);

        // 서브 카테고리 생성
        Category newSubCategory = addSubCategory(categoryCreateReqDto.getSubCategoryName(), newMainCategory);
        categoryRepository.save(newSubCategory);

        CategoryResDto categoryResDto = toCategoryResDto(newMainCategory);

        return categoryResDto;
    }

    // 서브 카테고리 생성
    @Transactional
    public CategoryResDto createSubCategory(CategoryCreateReqDto categoryCreateReqDto) {
        log.debug("카테고리 추가(service) - 서브 카테고리 추가 메서드 실행({})", categoryCreateReqDto.isCreatingMainCategory());

        Category mainCategory = categoryRepository.findByCategoryName(categoryCreateReqDto.getMainCategoryName());

        // 서브 카테고리 중복 검사
        List<Category> subCategories = mainCategory.getSubCategories();
        if(isCategoryNameDuplicate(categoryCreateReqDto.getSubCategoryName(), subCategories)){
            return null;
        }

        // 서브 카테고리 생성
        Category newSubCategory = addSubCategory(categoryCreateReqDto.getSubCategoryName(), mainCategory);
        categoryRepository.save(newSubCategory);

        CategoryResDto categoryResDto = toCategoryResDto(mainCategory);

        return categoryResDto;
    }

    // 카테고리 수정
    @Transactional
    public CategoryResDto updateCategory(CategoryUpdateReqDto categoryUpdateReqDto) {
        log.debug("카테고리 수정(service) - 요청 ID: {}, 새로운 이름: {}", categoryUpdateReqDto.getCategoryId(), categoryUpdateReqDto.getCategoryName());
        Category category = categoryRepository.findByCategoryId(categoryUpdateReqDto.getCategoryId());
        String updateCategoryName = categoryUpdateReqDto.getCategoryName();

        // 카테고리명 중복 검사
        if(category.getDepth() == 0){ // 메인 카테고리 수정인 경우
            List<Category> categories = categoryRepository.findByDepth(0);
            if (isCategoryNameDuplicate(updateCategoryName, categories)) {return null;}
        } else { // 서브 카테고리 수정인 경우
            List<Category> categories = category.getParentCategory().getSubCategories();
            if (isCategoryNameDuplicate(updateCategoryName, categories)) {return null;}
        }

        // 카테고리명 업데이트
        category.setCategoryName(updateCategoryName);
        categoryRepository.save(category);

        return toCategoryResDto(category);
    }

    // 카테고리 삭제
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        log.debug("카테고리 삭제(service) - categoryId: {}", categoryId);
        Category category = categoryRepository.findByCategoryId(categoryId);
        String categoryName = category.getCategoryName();

        log.debug("카테고리 삭제(service) - categoryName: {}", categoryName);

        Category parentCategory = category.getParentCategory();
        parentCategory.getSubCategories().remove(category); // 연관관계 삭제

        if(category != null) { // 항상 true
            // productList 비었는지 확인 후 비었으면
            categoryRepository.delete(category); // 서브 카테고리 삭제

            // 메인 카테고리에 서브 카테고리 없으면 메인 카테고리 삭제
            if(parentCategory != null) { // 항상 true
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

    // 서브 카테고리 생성 메서드
    private Category addSubCategory(String newCategoryName, Category parentCategory){
        Category newCategory = new Category();
        newCategory.setCategoryName(newCategoryName);
        newCategory.setDepth(parentCategory.getDepth() + 1); // 서브 카테고리의 깊이는 0 + 1
        newCategory.setParentCategory(parentCategory);
        parentCategory.getSubCategories().add(newCategory); // 부모-자식 동기화
        return newCategory;
    }

    // 카테고리명 중복 검사 메서드
    private boolean isCategoryNameDuplicate(String categoryName, List<Category> categories) {
        return categories.stream()
                .anyMatch(category -> category.getCategoryName().equals(categoryName));
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



    public Map<String, CategoryResDto> getMainAndSubCategoryById(Long categoryId) {
        // categoryId로 카테고리 조회
        Category subCategory = categoryRepository.findByCategoryIdWithParent(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid categoryId"));

        // 메인 카테고리는 subCategory의 parentCategory
        Category mainCategory = subCategory.getParentCategory();

        // 결과를 Map으로 반환
        Map<String, CategoryResDto> categoryMap = new HashMap<>();
        categoryMap.put("mainCategory", toCategoryResDto(mainCategory));
        categoryMap.put("subCategory", toCategoryResDto(subCategory));

        return categoryMap;
    }


    // 메인 카테고리 목록 조회
    public List<CategoryResDto> getMainCategories() {
        List<Category> mainCategories = categoryRepository.findByParentCategoryIsNull();
        return mainCategories.stream()
                .distinct() // 중복 제거
                .map(this::toCategoryResDto)
                .collect(Collectors.toList());
    }

    // 서브 카테고리 목록 조회
    public List<CategoryResDto> getSubCategoriesByParentId(Long parentId) {
        List<Category> subCategories = categoryRepository.findByParentCategory_CategoryId(parentId);
        return subCategories.stream()
                .map(this::toCategoryResDto)
                .collect(Collectors.toList());
    }
}
