package com.example.shop_project.payment.controller;
import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.payment.dto.PaymentDto;
import com.example.shop_project.payment.entity.Payment;
import com.example.shop_project.payment.service.PaymentService;
import com.example.shop_project.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/payments")
public class PaymentAPIController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@Validated @RequestBody PaymentDto paymentDto){
        Payment payment = paymentService.createPayment(paymentDto);
        return ResponseEntity.created(URI.create("/")).body(payment);
    }

    @PatchMapping("/decrease-product-stock")
    public ResponseEntity<Void> decreaseProductStock(@Validated @RequestBody List<OrderDetailDto> orderDetailDtoList){
        try {
            productService.decreaseProductStock(orderDetailDtoList);
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(URI.create("/")).build();
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/restore-product-stock")
    public ResponseEntity<Void> restoreProductStock(@Validated @RequestBody List<OrderDetailDto> orderDetailDtoList){
        productService.productStockRollback(orderDetailDtoList);
        return ResponseEntity.ok().build();
    }
}
