package com.example.shop_project.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductRequestDto {
    @NotBlank(message = "제품 이름은 필수 입력 항목입니다.")
    @Size(max = 20, message = "제품 이름은 최대 20자까지 가능합니다.")
    private String productName;

    @NotNull(message = "카테고리는 필수 선택 항목입니다.")
    private Long categoryId;

    @NotBlank(message = "상세 설명은 필수 입력 항목입니다.")
    @Size(max = 100, message = "상세 설명은 최대 100자까지 가능합니다.")
    private String description;

    @NotNull(message = "가격은 필수 입력 항목입니다.")
    @Min(value = 1, message = "가격은 1 이상이어야 합니다.")
    private int price;

    @NotEmpty(message = "옵션은 최소 1개 항목 이상 선택해야 합니다.")
    private List<ProductOptionDto> options;

    private String nickname;
}

