package com.example.shop_project.order.dto;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.entity.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OrderRequestDto {
    @Positive
    @NotNull
    private Long totalPrice;
    @NotNull
    private OrderStatus orderStatus;
    @NotNull
    private String requirement;
    @NotBlank
    private String address;
    @NotNull
    private String addressDetail;
    @NotBlank
    @Size(max = 5, min = 5)
    @Pattern(regexp = "^[0-9]*$")
    private String postNo;
    @NotNull
    private List<OrderDetailDto> orderDetailDtoList;
    @NotNull
    private Member member;
    @NotBlank
    private String contact;
    @NotBlank
    private String addressee;
}
