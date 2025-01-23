package com.example.shop_project.order.service;

import com.example.shop_project.order.dto.AddressDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.payment.repository.PaymentRepository;
import com.example.shop_project.product.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderManagementService {
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;

    public OrderManagementService(OrderMapper orderMapper, ProductRepository productRepository, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, PaymentRepository paymentRepository) {
        this.orderMapper = orderMapper;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto, productRepository);
        order.assignOrderToOrderDetail();
        Order newOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(newOrder);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderNo, OrderStatus orderStatus) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        order.updateStatus(orderStatus);
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(Long orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        orderDetailRepository.deleteByOrder(order);
        paymentRepository.deleteByOrder(order);
        orderRepository.deleteByOrderNo(orderNo);
    }

    @Transactional
    public void updateAddress(AddressDto addressDto){
        Order order = orderRepository.findByOrderNo(addressDto.getOrderNo()).orElseThrow();
        order.updateAddress(addressDto);
        orderRepository.save(order);
    }
}
