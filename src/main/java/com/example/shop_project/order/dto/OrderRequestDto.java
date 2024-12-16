package com.example.shop_project.order.dto;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.entity.PayMethod;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.security.Principal;
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
    private PayMethod payMethod;
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
    private List<OrderDetailDto> detailDtoList;
    private Member member;
    private Boolean deliveryFlag;

    public void setMember (Member member){
        this.member = member;
    }

    public void setAddress(String address, String addressDetail, String postNo){
        this.address = address;
        this.addressDetail = addressDetail;
        this.postNo = postNo;
    }
}
