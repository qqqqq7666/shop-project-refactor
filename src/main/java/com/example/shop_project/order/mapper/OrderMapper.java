package com.example.shop_project.order.mapper;

import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderRequestDto toRequestDto(Order order);
    OrderResponseDto toResponseDto(Order order);
    Order toEntity(OrderRequestDto orderRequestDto);
}
