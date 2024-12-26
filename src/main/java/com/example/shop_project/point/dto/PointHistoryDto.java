package com.example.shop_project.point.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointHistoryDto {
    @NotNull
    private PointHistoryRequestDto savedPointRequestDto;
    @NotNull
    private PointHistoryRequestDto usedPointRequestDto;
    @NotNull
    private Boolean isPaidWithPoint;
}
