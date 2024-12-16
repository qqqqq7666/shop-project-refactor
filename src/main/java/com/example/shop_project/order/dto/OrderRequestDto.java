package com.example.shop_project.order.dto;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.entity.PayMethod;
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
    @NotNull
    private Boolean deliveryFlag;
    @NotBlank
    private String contact;
    @NotBlank
    private String addressee;

    public void setMember (Member member){
        this.member = member;
    }

    public void setDelivery(String address, String addressDetail, String postNo, String addressee, String contact){
        this.address = address;
        this.addressDetail = addressDetail;
        this.postNo = postNo;
        this.addressee = addressee;
        this.contact = contact;
    }
}
