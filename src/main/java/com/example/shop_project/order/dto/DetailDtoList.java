package com.example.shop_project.order.dto;

import com.example.shop_project.order.entity.OrderDetail;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DetailDtoList {
    private List<OrderDetailDto> dtoList;
}
