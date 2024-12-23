package com.example.shop_project.order.mapper;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.dto.PaymentDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.Payment;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.repository.ProductRepository;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ProductRepository.class)
public interface OrderMapper {
    OrderRequestDto toRequestDto(Order order);
    OrderResponseDto toResponseDto(Order order);
    @Mapping(target = "orderDetailList", source = "orderDetailDtoList", qualifiedByName = "orderDetailMapper")
    Order toEntity(OrderRequestDto orderRequestDto, @Context ProductRepository productRepository);
    List<OrderResponseDto> toResponseDtoList(List<Order> orderList);

    OrderDetailDto toDto(OrderDetail orderDetail);

    @Mapping(target = "product", source = "productId", qualifiedByName = "detailProductMapper")
    OrderDetail toEntity(OrderDetailDto orderDetailDto, @Context ProductRepository productRepository);
    List<OrderDetail> toEntityList(List<OrderDetailDto> orderDetailDtoList);

    @Named("detailProductMapper")
    default Product detailProductMapper(Long productId, @Context ProductRepository productRepository){
        if(productId == null)
            return null;
        return productRepository.findById(productId).orElseThrow();
    }

    @Named("orderDetailMapper")
    default List<OrderDetail> orderDetailMapper(List<OrderDetailDto> orderDetailDtoList, @Context ProductRepository productRepository){
        if(orderDetailDtoList == null)
            return null;
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailDtoList.forEach(orderDetailDto -> {
            orderDetailList.add(toEntity(orderDetailDto, productRepository));
        });
        return orderDetailList;
    }


    PaymentDto toDto(Payment payment);
    Payment toEntity(PaymentDto paymentDto);
}
