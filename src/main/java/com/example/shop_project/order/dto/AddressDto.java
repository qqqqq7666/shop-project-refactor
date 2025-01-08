package com.example.shop_project.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddressDto {
    @NotNull
    private Long orderNo;
    @NotBlank
    private String addressee;
    @NotBlank
    private String contact;
    @NotBlank
    private String postNo;
    @NotBlank
    private String address;
    @NotNull
    private String addressDetail;
    @NotNull
    private String requirement;
}
