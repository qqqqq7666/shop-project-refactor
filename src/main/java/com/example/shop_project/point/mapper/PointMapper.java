package com.example.shop_project.point.mapper;

import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.repository.PointRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = OrderRepository.class)
public interface PointMapper {
    PointDto toDto(Point point);

//    @Mapping(target = "point", source = "pointId", qualifiedByName = "pointMapper")
    SavedPoint toEntity(SavedPointRequestDto savedPointRequestDto);
    SavedPointResponseDto toResponseDto(SavedPoint savedPoint);

    @Mapping(target = "order", source = "orderNo", qualifiedByName = "orderMapper")
    UsedPoint toEntity(UsedPointRequestDto usedPointRequestDto, @Context OrderRepository orderRepository);
    UsedPointResponseDto toResponseDto(UsedPoint usedPoint);

    @Named("orderMapper")
    default Order orderMapper(Long orderNo, @Context OrderRepository orderRepository){
        if(orderNo == null)
            return null;
        return orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }
}
