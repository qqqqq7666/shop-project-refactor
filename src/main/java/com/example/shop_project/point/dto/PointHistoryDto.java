package com.example.shop_project.point.dto;

import com.example.shop_project.point.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointHistoryDto {
    @NotNull
    private String email;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private Integer amount;
    @NotNull
    private Long orderNo;
}
