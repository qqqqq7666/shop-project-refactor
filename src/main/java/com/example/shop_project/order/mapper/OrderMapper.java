package com.example.shop_project.order.mapper;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.dto.PaymentDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderRequestDto toRequestDto(Order order);
    OrderResponseDto toResponseDto(Order order);
    Order toEntity(OrderRequestDto orderRequestDto);

    OrderDetailDto toDto(OrderDetail orderDetail);
    OrderDetail toEntity(OrderDetailDto orderDetailDto);

    PaymentDto toDto(Payment payment);
    Payment toEntity(PaymentDto paymentDto);
}
