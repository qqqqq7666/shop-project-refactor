package com.example.shop_project.point.dto;

import com.example.shop_project.point.entity.PointHistory;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.entity.UsedPoint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
    @NotNull
    private List<PointHistoryResponseDto> pointHistoryList;
}