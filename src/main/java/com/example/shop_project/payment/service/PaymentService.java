package com.example.shop_project.payment.service;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.payment.dto.PaymentDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.payment.entity.PayStatus;
import com.example.shop_project.payment.entity.Payment;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.payment.repository.PaymentRepository;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.ProductOption;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentManagementService paymentManagementService;
    private final PaymentRetrieveService paymentRetrieveService;

    @Transactional
    public Payment createPayment(PaymentDto paymentDto){
        return paymentManagementService.createPayment(paymentDto);
    }

    @Transactional
    public void cancelPay(Long orderNo){
        paymentManagementService.cancelPay(orderNo);
    }

    @Transactional
    public Payment getPaymentByOrderNo(Long orderNo){
        return paymentRetrieveService.getPaymentByOrderNo(orderNo);
    }
}
