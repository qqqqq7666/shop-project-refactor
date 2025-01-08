package com.example.shop_project.product.dto;

import com.example.shop_project.product.entity.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 포함한 생성자
public class ProductOptionDto {
    @NotNull(message = "사이즈는 선택 필수 항목입니다.")
    private Size size;

    @NotBlank(message = "색상은 선택 필수 항목입니다.")
    private String color;

    @Min(value = 1, message = "재고는 1 이상이어야 합니다.")
    private int stockQuantity;
}
