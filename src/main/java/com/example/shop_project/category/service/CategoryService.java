package com.example.shop_project.category.service;

import com.example.shop_project.category.dto.CategoryCreateReqDto;
import com.example.shop_project.category.dto.CategoryDeleteResDto;
import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.dto.CategoryUpdateReqDto;
import com.example.shop_project.category.entity.Category;
import com.example.shop_project.category.exception.CategoryCustomException;
import com.example.shop_project.category.exception.CategoryErrorCode;
import com.example.shop_project.category.repository.CategoryRepository;
import com.example.shop_project.product.repository.ProductRepository;
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
    private final ProductRepository productRepository;

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
        log.debug("카테고리 추가(service) - 메인 카테고리 추가 메서드 실행: {}", categoryCreateReqDto.isCreatingMainCategory());

        // 메인 카테고리명 중복 검사
        List<Category> mainCategories = categoryRepository.findByDepth(0); // 메인 카테고리
        if(isCategoryNameDuplicate(categoryCreateReqDto.getMainCategoryName(), mainCategories)){
            throw new CategoryCustomException(CategoryErrorCode.DUPLICATE_RESOURCE); // 중복 시 Conflict
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
        log.debug("카테고리 추가(service) - 메인 카테고리 추가 메서드 실행: {}", categoryCreateReqDto.isCreatingMainCategory());

        Category mainCategory = categoryRepository.findByCategoryName(categoryCreateReqDto.getMainCategoryName());

        // 서브 카테고리 중복 검사
        List<Category> subCategories = mainCategory.getSubCategories();
        if(isCategoryNameDuplicate(categoryCreateReqDto.getSubCategoryName(), subCategories)){
            throw new CategoryCustomException(CategoryErrorCode.DUPLICATE_RESOURCE); // 중복 시 Conflict
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

        List<Category> categories;
        if (category.getDepth() == 0) { // 메인 카테고리 수정인 경우
            categories = categoryRepository.findByDepth(0);
        } else { // 서브 카테고리 수정인 경우
            categories = category.getParentCategory().getSubCategories();
        }

        // 카테고리명 중복 검사
        if(isCategoryNameDuplicate(updateCategoryName, categories)) {
            throw new CategoryCustomException(CategoryErrorCode.DUPLICATE_RESOURCE); // 중복 시 Conflict
        }

        // 카테고리명 업데이트
        category.setCategoryName(updateCategoryName);
        categoryRepository.save(category);

        return toCategoryResDto(category);
    }

    // 카테고리 삭제
    @Transactional
    public CategoryDeleteResDto deleteCategory(Long categoryId) {
        log.debug("카테고리 삭제(service) - categoryId: {}", categoryId);
        Category category = categoryRepository.findByCategoryId(categoryId);
        log.debug("카테고리 삭제(service) - categoryName: {}", category.getCategoryName());

        CategoryDeleteResDto categoryDeleteResDto = new CategoryDeleteResDto();
        categoryDeleteResDto.setSubCategoryId(categoryId);

        Category parentCategory = category.getParentCategory();
        categoryDeleteResDto.setMainCategoryId(parentCategory.getCategoryId());
        parentCategory.getSubCategories().remove(category); // 연관관계 삭제

        categoryRepository.delete(category); // 서브 카테고리 삭제

        // 메인 카테고리에 서브 카테고리 없으면 메인 카테고리 삭제
        List<Category> subCategories = parentCategory.getSubCategories();
        if(subCategories.isEmpty()) {
            categoryDeleteResDto.setExistMainCategory(false);
            categoryRepository.delete(parentCategory); // 메인 카테고리 삭제
        }
        else {
            categoryDeleteResDto.setExistMainCategory(true);
        }
        return categoryDeleteResDto;
    }

    // 카테고리 삭제 시 상품 존재 여부 확인
    public void existsProduct(Long categoryId) {
        boolean hasProduct = productRepository.existsByCategoryId(categoryId);
        if(hasProduct){
            throw new CategoryCustomException(CategoryErrorCode.CONFLICT_WITH_PRODUCTS);
        }
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
                .anyMatch(category -> category.getCategoryName().equalsIgnoreCase(categoryName));
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
