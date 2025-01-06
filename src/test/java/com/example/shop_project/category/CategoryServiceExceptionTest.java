package com.example.shop_project.category;

import com.example.shop_project.category.dto.CategoryCreateReqDto;
import com.example.shop_project.category.dto.CategoryUpdateReqDto;
import com.example.shop_project.category.entity.Category;
import com.example.shop_project.category.exception.CategoryCustomException;
import com.example.shop_project.category.repository.CategoryRepository;
import com.example.shop_project.category.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceExceptionTest {
    private MockMvc mockMvc;

    @Mock
    private CategoryRepository categoryRepository;

    // CategoryController에 Mock 객체를 주입
    @InjectMocks
    private CategoryService categoryService;

    private Category mainCategory;
    private Category subCategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryService).build();

        subCategory = Category.builder()
                .categoryId(2L)
                .categoryName("SubCategory")
                .depth(1)
                .build();

        List<Category> subCategories = new ArrayList<>();
        subCategories.add(subCategory);

        mainCategory = Category.builder()
                .categoryId(1L)
                .categoryName("MainCategory")
                .depth(0)
                .subCategories(subCategories)
                .build();

        subCategory.setParentCategory(mainCategory);
    }

    @Test
    void createMainCategory_duplicateName() {
        CategoryCreateReqDto reqDto = new CategoryCreateReqDto("MainCategory", "NewSubCategory", true);

        List<Category> mainCategories = Arrays.asList(mainCategory);
        Mockito.when(categoryRepository.findByDepth(0)).thenReturn(mainCategories);

        assertThrows(CategoryCustomException.class, () -> categoryService.createMainCategory(reqDto));
    }

    @Test
    void createSubCategory_duplicateName() {
        CategoryCreateReqDto reqDto = new CategoryCreateReqDto("MainCategory", "SubCategory", false);

        Mockito.when(categoryRepository.findByCategoryNameAndDepth(reqDto.getMainCategoryName(), 0)).thenReturn(mainCategory);

        assertThrows(CategoryCustomException.class, () -> categoryService.createSubCategory(reqDto));
    }

    @Test
    void updateCategory_duplicateName() {
        CategoryUpdateReqDto updateReqDto = new CategoryUpdateReqDto(1L, "MainCategory");

        Mockito.when(categoryRepository.findByCategoryId(updateReqDto.getCategoryId())).thenReturn(mainCategory);

        List<Category> mainCategories = Arrays.asList(mainCategory);
        Mockito.when(categoryRepository.findByDepth(0)).thenReturn(mainCategories);

        assertThrows(CategoryCustomException.class, () -> categoryService.updateCategory(updateReqDto));
    }
}
