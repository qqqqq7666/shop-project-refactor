package com.example.shop_project.order.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.dto.AddressDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.CanceledOrder;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.CanceledOrderRepository;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.order.repository.PaymentRepository;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.repository.UsedPointRepository;
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
    @Autowired
    private CanceledOrderRepository canceledOrderRepository;
    @Autowired
    private UsedPointRepository usedPointRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto, productRepository);
        order.assignOrderToOrderDetail();
        Order newOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(newOrder);
    }

    public List<OrderDetail> getOrderDetailList(Long orderNo) {
        Order foundOrder = orderRepository.findByOrderNo(orderNo).orElseThrow();
        return orderDetailRepository.findAllByOrder(foundOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderPageList(Principal principal, Pageable pageable, String keyword) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        Page<Order> orderPage = orderRepository.findAllByMemberAndOrderStatusNotAndOrderDetailListProductProductNameContainingOrderByOrderNoDesc(member, OrderStatus.FAIL, keyword, pageable);
        log.info("orderPage size = {}", orderPage.getTotalElements());

        return orderPage.map(order -> orderMapper.toResponseDto(order));
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
        return orderRepository.findFirstByOrderByOrderNoDesc().orElseGet(() -> Order.builder().orderNo(0L).build());
    }

    public CanceledOrder createCanceledOrder(Long orderNo, String reason) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        return canceledOrderRepository.save(CanceledOrder.builder()
                .order(order)
                .reason(reason)
                .build());
    }

    public CanceledOrder getCanceledOrder(Long orderNo){
        return canceledOrderRepository.findById(orderNo).orElseGet(() -> null);
    }

    @Transactional
    public void refund(Long orderNo){
        CanceledOrder canceledOrder = canceledOrderRepository.findById(orderNo).orElseThrow();
        Order order = canceledOrder.getOrder();
        canceledOrder.confirmRequire();
        if(order.getIsPaidWithPoint()){
            UsedPoint usedPoint = usedPointRepository.findByOrder(order).orElseThrow();
            usedPoint.getPoint().rollbackBalance(-usedPoint.getAmount());
            usedPointRepository.delete(usedPoint);
        }
        canceledOrderRepository.save(canceledOrder);
        order.updateStatus(OrderStatus.REFUND);
        orderRepository.save(order);
    }

    //mypage
    public Integer getOrderCountByEmail(String email){
        return orderRepository.findAllByMember(memberRepository.findByEmail(email).orElseThrow()).size();
    }

    @Transactional
    public void updateAddress(AddressDto addressDto){
        Order order = orderRepository.findByOrderNo(addressDto.getOrderNo()).orElseThrow();
        order.updateAddress(addressDto);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteCanceledOrder(Long orderNo){
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        order.updateStatus(OrderStatus.NEW);
        orderRepository.save(order);
        CanceledOrder canceledOrder = canceledOrderRepository.findById(orderNo).orElseThrow();
        canceledOrderRepository.delete(canceledOrder);
    }

    public Page<OrderResponseDto> getTotalOrderPage(String email, String orderStatus, Pageable pageable){
        if(orderStatus.equals("all"))
            return orderRepository.findAllByMemberEmailContainingOrderByOrderNoDesc(email, pageable).map(order -> orderMapper.toResponseDto(order));
        else
            return orderRepository.findAllByOrderStatusAndMemberEmailContainingOrderByOrderNoDesc(OrderStatus.valueOf(orderStatus), email, pageable)
                    .map(order -> orderMapper.toResponseDto(order));
    }
}
