package com.example.shop_project.point.dto;

import com.example.shop_project.point.entity.SaveReason;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavedPointRequestDto {
    @NotNull
    private String email;
    @NotNull
    private String saveReason;
    @NotNull
    private Integer savedPoint;
}
