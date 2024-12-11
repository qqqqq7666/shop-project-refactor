package com.example.shop_project.order.service;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired private OrderMapper orderMapper;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto){
        Order newOrder = orderMapper.toEntity(orderRequestDto);
        return orderMapper.toResponseDto(orderRepository.save(newOrder));
    }

    public List<OrderDetailDto> getOrderDetailList(Long orderNo){
        Order foundOrder = orderRepository.findByOrderNo(orderNo).orElseThrow();
        List<OrderDetail> detailList = orderDetailRepository.findAllByOrder(foundOrder);
        List<OrderDetailDto> detailDtoList = new ArrayList<>();
        for(OrderDetail orderDetail : detailList)
            detailDtoList.add(orderMapper.toDto(orderDetail));
        return detailDtoList;
    }

    public List<OrderResponseDto> getOrderList(){
        List<OrderResponseDto> response = new ArrayList<>();
        for(Order order : orderRepository.findAll())
            response.add(orderMapper.toResponseDto(order));
        return response;
    }

    public OrderResponseDto getOrderByOrderNo(Long orderNo){
        return orderMapper.toResponseDto(orderRepository.findByOrderNo(orderNo).orElseThrow());
    }

    public void deleteOrder(Long orderNo){
        orderRepository.deleteByOrderNo(orderNo);
    }
}
