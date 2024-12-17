package com.example.shop_project.order.service;

import com.example.shop_project.order.entity.Payment;
import com.example.shop_project.order.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    public Payment createPayment(Payment payment){
        return paymentRepository.save(payment);
    }
}
