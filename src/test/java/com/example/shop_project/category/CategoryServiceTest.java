package com.example.shop_project.category;

import com.example.shop_project.category.dto.CategoryCreateReqDto;
import com.example.shop_project.category.dto.CategoryDeleteResDto;
import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.dto.CategoryUpdateReqDto;
import com.example.shop_project.category.entity.Category;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
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
    void getAllCategories() {
        Mockito.when(categoryRepository.findByDepth(0)).thenReturn(Arrays.asList(mainCategory));

        List<CategoryResDto> result = categoryService.getAllCategories();

        // 메인 카테고리 검증
        assertEquals(1, result.size());
        assertEquals("MainCategory", result.get(0).getCategoryName());
        // 서브 카테고리 검증
        assertEquals(1, result.get(0).getSubCategories().size());
        assertEquals("SubCategory", result.get(0).getSubCategories().get(0).getCategoryName());
    }

    @Test
    void createMainCategory() {
        CategoryCreateReqDto reqDto = new CategoryCreateReqDto("NewMainCategory", "NewSubCategory", true);
        Mockito.when(categoryRepository.findByDepth(0)).thenReturn(Collections.emptyList());
        Mockito.when(categoryRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResDto result = categoryService.createMainCategory(reqDto);

        // 메인 카테고리 검증
        assertNotNull(result);
        assertEquals("NewMainCategory", result.getCategoryName());
        // 서브 카테고리 검증
        assertNotNull(result.getSubCategories());
        assertEquals(1, result.getSubCategories().size());
        assertEquals("NewSubCategory", result.getSubCategories().get(0).getCategoryName());
    }

    @Test
    void createSubCategory() {
        // given
        CategoryCreateReqDto reqDto = new CategoryCreateReqDto("MainCategory", "TwoSubCategory", false);
        Mockito.when(categoryRepository.findByCategoryName("MainCategory")).thenReturn(mainCategory);

        // when
        CategoryResDto result = categoryService.createSubCategory(reqDto);

        // then
        // 메인 카테고리 검증
        assertNotNull(result);
        assertEquals("MainCategory", result.getCategoryName());
        // 서브 카테고리 검증
        assertNotNull(result.getSubCategories());
        assertEquals(2, result.getSubCategories().size());
        assertEquals("TwoSubCategory", result.getSubCategories().get(1).getCategoryName());

        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any(Category.class));
    }


    @Test
    void updateCategory() {
        CategoryUpdateReqDto updateReqDto = new CategoryUpdateReqDto(1L, "UpdatedName");
        Mockito.when(categoryRepository.findByCategoryId(1L)).thenReturn(mainCategory);
        Mockito.when(categoryRepository.save(Mockito.any())).thenReturn(mainCategory);

        CategoryResDto result = categoryService.updateCategory(updateReqDto);

        assertNotNull(result);
        assertEquals("UpdatedName", result.getCategoryName());
        assertEquals(mainCategory.getCategoryId(), result.getCategoryId());
    }

    @Test
    void deleteCategory() {
        Mockito.when(categoryRepository.findByCategoryId(2L)).thenReturn(subCategory); // 삭제 전

        CategoryDeleteResDto result = categoryService.deleteCategory(2L);

        Mockito.verify(categoryRepository, Mockito.times(1)).delete(subCategory);
        if (mainCategory.getSubCategories().isEmpty()) {
            Mockito.verify(categoryRepository, Mockito.times(1)).delete(mainCategory);
        } else {
            Mockito.verify(categoryRepository, Mockito.never()).delete(mainCategory);
        }
    }

}