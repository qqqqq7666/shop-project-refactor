package com.example.shop_project.point.dto;

import com.example.shop_project.point.entity.SaveReason;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SavedPointResponseDto {
    private SaveReason saveReason;
    private Integer savedPoint;
    private LocalDateTime createdDate;
}
