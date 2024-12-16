package com.example.shop_project.order.dto;

import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.entity.PayMethod;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponseDto {
    @NotBlank
    private Long orderNo;
    @NotNull
    @Positive
    private Long totalPrice;
    @NotNull
    private OrderStatus orderStatus;
    @NotNull
    private PayMethod payMethod;
    @NotNull
    private String requirement;
    @NotBlank
    private String address;
    @NotNull
    private String addressDetail;
    @NotBlank
    @Size(max = 5, min = 5)
    @Pattern(regexp = "^[0-9]*$")
    private String postNo;
    private LocalDateTime createdDate;
}
