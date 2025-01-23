package com.example.shop_project.admin.controller;

import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderAPIController {
    @Autowired
    OrderService orderService;
    @Autowired
    private PaymentService paymentService;

    @PatchMapping("/{orderNo}/update-status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long orderNo, @RequestBody OrderStatus orderStatus){
        OrderResponseDto response = orderService.updateOrderStatus(orderNo, orderStatus);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }

    @DeleteMapping("/{orderNo}/delete")
    public RedirectView deleteOrder(@PathVariable Long orderNo){
        orderService.deleteOrder(orderNo);
        return new RedirectView("/admin/orders");
    }

    @GetMapping("/{orderNo}")
    public ResponseEntity<List<OrderDetail>> orderDetail(@PathVariable Long orderNo){
        return ResponseEntity.ok(orderService.getOrderDetailList(orderNo));
    }

    @PatchMapping("/refund")
    public RedirectView refund(@RequestParam Long orderNo){
        orderService.refund(orderNo);
        paymentService.cancelPay(orderNo);
        return new RedirectView("/admin/orders/" + orderNo);
    }
}
