package com.example.shop_project.order.service;

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
import com.example.shop_project.point.repository.UsedPointRepository;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@Slf4j
public class OrderService {
    private final OrderRefundService orderRefundService;
    private final OrderManagementService orderManagementService;
    private final OrderRetrieveService orderRetrieveService;

    public OrderService(OrderRefundService orderRefundService, OrderManagementService orderManagementService, OrderRetrieveService orderRetrieveService) {
        this.orderRefundService = orderRefundService;
        this.orderManagementService = orderManagementService;
        this.orderRetrieveService = orderRetrieveService;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        return orderManagementService.createOrder(orderRequestDto);
    }

    public List<OrderDetail> getOrderDetailList(Long orderNo) {
        return orderRetrieveService.getOrderDetailList(orderNo);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderPageList(Principal principal, Pageable pageable, String keyword) {
        return orderRetrieveService.getOrderPageList(principal, pageable, keyword);
    }

    public OrderResponseDto getOrderByOrderNo(Long orderNo) {
        return orderRetrieveService.getOrderByOrderNo(orderNo);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderNo, OrderStatus orderStatus) {
        return orderManagementService.updateOrderStatus(orderNo, orderStatus);
    }

    @Transactional
    public void deleteOrder(Long orderNo) {
        orderManagementService.deleteOrder(orderNo);
    }

    public Order getRecentOrder() {
        return orderRetrieveService.getRecentOrder();
    }

    public CanceledOrder createCanceledOrder(Long orderNo, String reason) {
        return orderRefundService.createCanceledOrder(orderNo, reason);
    }

    public CanceledOrder getCanceledOrder(Long orderNo){
        return orderRefundService.getCanceledOrder(orderNo);
    }

    @Transactional
    public void refund(Long orderNo){
        orderRefundService.refund(orderNo);
    }

    public Integer getOrderCountByEmail(String email){
        return orderRetrieveService.getOrderCountByEmail(email);
    }

    @Transactional
    public void updateAddress(AddressDto addressDto){
        orderManagementService.updateAddress(addressDto);
    }

    @Transactional
    public void deleteCanceledOrder(Long orderNo){
        orderRefundService.deleteCanceledOrder(orderNo);
    }

    public Page<OrderResponseDto> getTotalOrderPage(String email, String orderStatus, Pageable pageable){
        return orderRetrieveService.getTotalOrderPage(email, orderStatus, pageable);
    }
}
