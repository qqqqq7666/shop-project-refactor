package com.example.shop_project.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.shop_project.category.controller.CategoryAPIController;
import com.example.shop_project.category.dto.CategoryDeleteResDto;
import com.example.shop_project.category.dto.CategoryResDto;
import com.example.shop_project.category.dto.CategoryUpdateReqDto;
import com.example.shop_project.category.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    // CategoryController에 Mock 객체를 주입
    @InjectMocks
    private CategoryAPIController categoryController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("카테고리 리스트 조회 테스트")
    void getCategoryList() throws Exception {
        CategoryResDto subCategory1 = new CategoryResDto(2L, "SubCategoryOne", 1, new ArrayList<>());
        CategoryResDto subCategory2 = new CategoryResDto(3L, "SubCategoryTwo", 1, new ArrayList<>());
        List<CategoryResDto> subCategories = Arrays.asList(subCategory1, subCategory2);

        CategoryResDto mainCategory1 = new CategoryResDto(1L, "MainCategory", 0, subCategories);
        List<CategoryResDto> categoryList = Arrays.asList(mainCategory1);

        Mockito.when(categoryService.getAllCategories()).thenReturn(categoryList);

        mockMvc.perform(get("/admin/category/categoryList"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/categoryList"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", categoryList));
    }

    @Test
    @DisplayName("메인 카테고리 생성 테스트")
    void createMainCategory() throws Exception {
        //CategoryCreateReqDto createReq = new CategoryCreateReqDto("MainCategory", "SubCategory", true);

        CategoryResDto subCategory = new CategoryResDto(2L, "SubCategory", 1, new ArrayList<>());
        List<CategoryResDto> subCategories = Arrays.asList(subCategory);

        CategoryResDto resDto = new CategoryResDto(1L, "MainCategory", 0, subCategories);

        Mockito.when(categoryService.createMainCategory(Mockito.any())).thenReturn(resDto);

        mockMvc.perform(post("/admin/category/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("mainCategoryName", "MainCategory")
                        .param("subCategoryName", "SubCategory")
                        .param("creatingMainCategory", "true")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("서브 카테고리 생성 테스트")
    void createSubCategory() throws Exception {
        //CategoryCreateReqDto createReq = new CategoryCreateReqDto("MainCategory", "SubCategory", true);

        CategoryResDto subCategory = new CategoryResDto(2L, "SubCategory", 1, new ArrayList<>());
        List<CategoryResDto> subCategories = Arrays.asList(subCategory);

        CategoryResDto resDto = new CategoryResDto(1L, "MainCategory", 0, subCategories);

        Mockito.when(categoryService.createSubCategory(Mockito.any())).thenReturn(resDto);

        mockMvc.perform(post("/admin/category/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("mainCategoryName", "MainCategory")
                        .param("subCategoryName", "SubCategory")
                        .param("creatingMainCategory", "false")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    void updateCategory() throws Exception {
        CategoryUpdateReqDto updateReq = new CategoryUpdateReqDto(1L, "UpdatedName");
        CategoryResDto resDto = new CategoryResDto(1L, "UpdatedName", 1, null);

        Mockito.when(categoryService.updateCategory(Mockito.any())).thenReturn(resDto);

        mockMvc.perform(patch("/admin/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("UpdatedName"));
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategory() throws Exception {
        Map<String, Object> request = Map.of("categoryId", 2L);
        CategoryDeleteResDto categoryDeleteResDto = new CategoryDeleteResDto(2L, 1L, false);
        Mockito.when(categoryService.deleteCategory(2L)).thenReturn(categoryDeleteResDto);

        mockMvc.perform(delete("/admin/category/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
