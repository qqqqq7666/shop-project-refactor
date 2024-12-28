package com.example.shop_project.product;

import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Size;
import com.example.shop_project.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ProductCreateControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // MockMvc 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testCreateProduct() throws Exception {
        // Mock Product Option
        ProductOptionDto optionDto = new ProductOptionDto(Size.M, "Red", 10);

        // Mock Product Request DTO
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Test Product");
        requestDto.setDescription("This is a test product.");
        requestDto.setPrice(1000);
        requestDto.setOptions(Collections.singletonList(optionDto));

        // Serialize ProductRequestDto to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String productRequestDtoJson = objectMapper.writeValueAsString(requestDto);

        // Mock Image File
        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );

        // Mock ProductRequestDto JSON File
        MockMultipartFile productRequestDtoFile = new MockMultipartFile(
                "productRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                productRequestDtoJson.getBytes()
        );

        // Mock Service Response
        ProductResponseDto responseDto = ProductResponseDto.builder()
                .productId(1L)
                .productName("Test Product")
                .description("This is a test product.")
                .price(1000)
                .salesCount(5)
                .viewCount(10)
                .imageUrls(Collections.singletonList("/upload/products/test-image.jpg"))
                .options(Collections.singletonList(optionDto))
                .build();

        when(productService.createProduct(any(ProductRequestDto.class), anyList()))
                .thenReturn(responseDto);

        // Perform Multipart Request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/products")
                        .file(productRequestDtoFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product created successfully!"));

        // Verify service interaction
        Mockito.verify(productService).createProduct(any(ProductRequestDto.class), anyList());
    }
}

