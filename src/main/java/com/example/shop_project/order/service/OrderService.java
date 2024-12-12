package com.example.shop_project.order.service;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductRepository productRepository;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order newOrder = orderRepository.save(orderMapper.toEntity(orderRequestDto));
        createOrderDetail(newOrder, orderRequestDto.getDetailDtoList());
        return orderMapper.toResponseDto(newOrder);
    }

    public void createOrderDetail(Order order, List<OrderDetailDto> dtoList) {
        for(OrderDetailDto dto : dtoList){
            OrderDetail orderDetail = orderMapper.toEntity(dto);
            orderDetail.setOrderAndProduct(order, productRepository.findById(dto.getProductId()).orElseThrow());

            orderDetailRepository.save(orderDetail);
        }
    }

    public List<OrderDetail> getOrderDetailList(Long orderNo) {
        Order foundOrder = orderRepository.findByOrderNo(orderNo).orElseThrow();
        List<OrderDetail> detailList = orderDetailRepository.findAllByOrder(foundOrder);
//        List<OrderDetailDto> detailDtoList = new ArrayList<>();
//        for (OrderDetail orderDetail : detailList)
//            detailDtoList.add(orderMapper.toDto(orderDetail));
        return detailList;
    }

    public List<OrderDetail> getAllDetails(){
        return orderDetailRepository.findAll();
    }

    public List<OrderResponseDto> getOrderList() {
        List<OrderResponseDto> response = new ArrayList<>();
        for (Order order : orderRepository.findAll())
            response.add(orderMapper.toResponseDto(order));
        return response;
    }

    public OrderResponseDto getOrderByOrderNo(Long orderNo) {
        return orderMapper.toResponseDto(orderRepository.findByOrderNo(orderNo).orElseThrow());
    }

    public OrderResponseDto updateOrder(Long orderNo, OrderRequestDto request){
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        order.updateOrder(request);
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(Long orderNo) {
        orderRepository.deleteByOrderNo(orderNo);
    }
}
