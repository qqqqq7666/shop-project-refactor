package com.example.shop_project.payment.service;

import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.payment.dto.PaymentDto;
import com.example.shop_project.payment.entity.Payment;
import com.example.shop_project.payment.repository.PaymentRepository;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class PaymentManagementService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(PaymentDto paymentDto){
        Payment newPayment = orderMapper.toEntity(paymentDto);
        Order order = orderRepository.findFirstByOrderByOrderNoDesc().orElseThrow(() -> new NoSuchElementException("주문이 존재하지 않습니다."));
        newPayment.assignOrderToCreate(order);
        order.getOrderDetailList().forEach(orderDetail -> {
            Product product = orderDetail.getProduct();
            product.setSalesCount((int) (product.getSalesCount() + orderDetail.getQuantity()));
            productRepository.save(product);
        });
        return paymentRepository.save(newPayment);
    }

    @Transactional
    public void cancelPay(Long orderNo){
        Payment payment = paymentRepository.findByOrder(orderRepository.findByOrderNo(orderNo).orElseThrow()).orElseThrow();
        payment.cancelPay();
        paymentRepository.save(payment);
    }
}
