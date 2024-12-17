package com.example.shop_project.order.dto;

import com.example.shop_project.order.entity.PG;
import com.example.shop_project.order.entity.PayMethod;
import com.example.shop_project.order.entity.PayStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentDto {
    @NotBlank
    private String memberName;
    @NotNull
    private Long amount;
    @NotNull
    private PG pg;
    @NotNull
    private PayMethod payMethod;
    @NotNull
    private PayStatus payStatus;
}
