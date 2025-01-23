package com.example.shop_project.payment.service;

import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.payment.entity.PayStatus;
import com.example.shop_project.payment.entity.Payment;
import com.example.shop_project.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentRetrieveService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment getPaymentByOrderNo(Long orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        return paymentRepository.findByOrder(order).orElseGet(() -> Payment.builder()
                .payStatus(PayStatus.FAIL).build());

    }
}
