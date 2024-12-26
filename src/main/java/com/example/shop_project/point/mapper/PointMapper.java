package com.example.shop_project.point.mapper;

import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.PointHistory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = OrderRepository.class)
public interface PointMapper {
    PointDto toDto(Point point);

    @Mapping(target = "order", source = "orderNo", qualifiedByName = "orderMapper")
    PointHistory toEntity(PointHistoryRequestDto pointHistoryRequestDto, @Context OrderRepository orderRepository);
    List<PointHistoryResponseDto> toResponseDto(List<PointHistory> pointHistoryList);

    @Named("orderMapper")
    default Order orderMapper(Long orderNo, @Context OrderRepository orderRepository){
        if(orderNo == null)
            return null;
        return orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }
}
