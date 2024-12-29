package com.example.shop_project.point.dto;

import com.example.shop_project.order.entity.Order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PointHistoryDto {
    @NotNull
    @Positive
    private Integer amount;
    private LocalDateTime createdDate;
    private String reason;
    private Order order;
    private Boolean isUsed;
}
