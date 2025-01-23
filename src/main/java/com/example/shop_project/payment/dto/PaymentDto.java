package com.example.shop_project.payment.dto;

import com.example.shop_project.payment.entity.PayMethod;
import com.example.shop_project.payment.entity.PayStatus;
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
    private PayMethod payMethod;
    @NotNull
    private PayStatus payStatus;
}
