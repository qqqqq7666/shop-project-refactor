package com.example.shop_project.order.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class OrderRetrieveService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MemberRepository memberRepository;

    public OrderRetrieveService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, MemberRepository memberRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.memberRepository = memberRepository;
        this.orderMapper = orderMapper;
    }

    public List<OrderDetail> getOrderDetailList(Long orderNo) {
        Order foundOrder = orderRepository.findByOrderNo(orderNo).orElseThrow();
        return orderDetailRepository.findAllByOrder(foundOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderPageList(Principal principal, Pageable pageable, String keyword) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(() -> new IllegalArgumentException("member doesn't exist"));
        Page<Order> orderPage = orderRepository.findAllByMemberAndOrderStatusNotAndOrderDetailListProductProductNameContainingOrderByOrderNoDesc(member, OrderStatus.FAIL, keyword, pageable);

        return orderPage.map(orderMapper::toResponseDto);
    }

    public OrderResponseDto getOrderByOrderNo(Long orderNo) {
        return orderMapper.toResponseDto(orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("order doesn't exist")));
    }

    public Order getRecentOrder() {
        return orderRepository.findFirstByOrderByOrderNoDesc().orElseGet(() -> Order.builder().orderNo(0L).build());
    }

    public Integer getOrderCountByEmail(String email){
        return orderRepository.findAllByMember(memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("member doesn't exist"))).size();
    }

    public Page<OrderResponseDto> getTotalOrderPage(String email, String orderStatus, Pageable pageable){
        if(orderStatus.equals("all"))
            return orderRepository.findAllByMemberEmailContainingOrderByOrderNoDesc(email, pageable).map(orderMapper::toResponseDto);
        else
            return orderRepository.findAllByOrderStatusAndMemberEmailContainingOrderByOrderNoDesc(OrderStatus.valueOf(orderStatus), email, pageable)
                    .map(orderMapper::toResponseDto);
    }
}
