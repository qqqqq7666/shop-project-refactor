package com.example.shop_project.product;

import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.Size;
import com.example.shop_project.product.repository.ProductRepository;
import com.example.shop_project.product.service.ImageService;
import com.example.shop_project.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProduct() {
        // 1. 데이터 준비
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setCategoryName("상의");
        productRequestDto.setProductName("테스트 제품");
        productRequestDto.setPrice(10000);
        productRequestDto.setDescription("테스트 설명");
        productRequestDto.setOptions(List.of(new ProductOptionDto(Size.M, "Blue", 5)));

        MockMultipartFile imageFile1 = new MockMultipartFile(
                "images", "test1.jpg", "image/jpeg", "dummy data".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile(
                "images", "test2.jpg", "image/jpeg", "dummy data".getBytes());
        List<MockMultipartFile> images = List.of(imageFile1, imageFile2);

        // 2. Mocking
        when(imageService.uploadImage(anyString(), any(MultipartFile.class)))
                .thenReturn("/upload/products/test.jpg");
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setProductId(1L);
                    return product;
                });

        // 3. 서비스 메서드 호출
        productService.createProduct(productRequestDto, List.of(imageFile1, imageFile2));

        // 4. 검증
        verify(imageService, times(2)).uploadImage(anyString(), any());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
