package com.example.shop_project.point.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointDto {
    @NotNull
    @Positive
    private Integer balance;
    @NotNull
    @Positive
    private Integer totalSavedPoint;
    @NotNull
    @Positive
    private Integer totalUsedPoint;
}
