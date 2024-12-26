package com.example.shop_project.point.dto;

import com.example.shop_project.point.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryResponseDto {
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private Integer amount;
    @NotNull
    private Long orderNo;
    @NotNull
    private LocalDateTime createdDate;
}
