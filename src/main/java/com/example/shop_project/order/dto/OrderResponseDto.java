package com.example.shop_project.order.dto;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponseDto {
    @NotBlank
    private Long orderNo;
    @NotNull
    @Positive
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
    private LocalDateTime createdDate;
    @NotBlank
    private String contact;
    @NotBlank
    private String addressee;
    @NotNull
    private Member member;
    @NotNull
    private List<OrderDetail> orderDetailList;
    @NotNull
    private Boolean isPaidWithPoint;

}
