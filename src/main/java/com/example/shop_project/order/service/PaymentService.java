package com.example.shop_project.order.service;

import com.example.shop_project.order.dto.PaymentDto;
import com.example.shop_project.order.entity.Payment;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.order.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderMapper orderMapper;

    public Payment createPayment(PaymentDto paymentDto){
        Payment newPayment = orderMapper.toEntity(paymentDto);
        newPayment.assignOrderToCreate(orderRepository.findFirstByOrderByOrderNoDesc().orElseThrow(() -> new NoSuchElementException("주문이 존재하지 않습니다.")));
        return paymentRepository.save(newPayment);
    }
}
