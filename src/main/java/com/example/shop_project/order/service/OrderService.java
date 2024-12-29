package com.example.shop_project.order.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.order.repository.PaymentRepository;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto, productRepository);
        order.assignOrderToOrderDetail();
        Order newOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(newOrder);
    }

    public List<OrderDetail> getOrderDetailList(Long orderNo) {
        Order foundOrder = orderRepository.findByOrderNo(orderNo).orElseThrow();
        List<OrderDetail> detailList = orderDetailRepository.findAllByOrder(foundOrder);
        return detailList;
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderPageList(Principal principal, Pageable pageable) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Page<Order> orderPage = orderRepository.findAllByMemberAndOrderStatusNotOrderByOrderNoDesc(member, pageable, OrderStatus.FAIL);
        Page<OrderResponseDto> orderResponseDtoPage = orderPage.map(order -> orderMapper.toResponseDto(order));

        return orderResponseDtoPage;
    }

    public List<OrderResponseDto> getOrderList() {
        List<OrderResponseDto> response = new ArrayList<>();
        for (Order order : orderRepository.findAllByOrderByOrderNoDesc())
            response.add(orderMapper.toResponseDto(order));
        return response;
    }

    public OrderResponseDto getOrderByOrderNo(Long orderNo) {
        return orderMapper.toResponseDto(orderRepository.findByOrderNo(orderNo).orElseThrow());
    }

    @Transactional
    public OrderResponseDto updateOrder(Long orderNo, OrderRequestDto request) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        order.updateOrder(request);
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    // updateStatus를 따로 구현하는게 맞는지..
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

    public Order getRecentOrder() {
        return orderRepository.findFirstByOrderByOrderNoDesc().orElseThrow(() -> new NoSuchElementException("주문이 존재하지 않습니다."));
    }
}
