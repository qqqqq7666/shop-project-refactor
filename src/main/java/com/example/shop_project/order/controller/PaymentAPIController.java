package com.example.shop_project.order.controller;
import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.PaymentDto;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.Payment;
import com.example.shop_project.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/payment")
public class PaymentAPIController {
    @Autowired
    PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@Validated @RequestBody PaymentDto paymentDto){
        Payment payment = paymentService.createPayment(paymentDto);
        return ResponseEntity.created(URI.create("/")).body(payment);
    }

    @PatchMapping("/update-product-stock")
    public ResponseEntity<Void> updateProductStock(List<OrderDetailDto> orderDetailDtoList){
        paymentService.productStockUpdate(orderDetailDtoList);
        return ResponseEntity.ok().build();
    }
}
