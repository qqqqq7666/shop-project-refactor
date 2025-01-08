package com.example.shop_project.order.controller;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.dto.AddressDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.CanceledOrder;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderAPIController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;

    @GetMapping("/{orderNo}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable(name = "orderNo") Long orderNo){
        OrderResponseDto response = orderService.getOrderByOrderNo(orderNo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderNo}/order-detail")
    public ResponseEntity<List<OrderDetail>> getOrderDetails(@PathVariable("orderNo") Long orderNo){
        return ResponseEntity.ok(orderService.getOrderDetailList(orderNo));
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@Validated @RequestBody OrderRequestDto orderRequestDto){
        OrderResponseDto response = orderService.createOrder(orderRequestDto);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }

    @DeleteMapping("{orderNo}/delete")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderNo") Long orderNo){
        orderService.deleteOrder(orderNo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderNo}/update-status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable("orderNo") Long orderNo, @RequestBody OrderStatus orderStatus){
        OrderResponseDto response = orderService.updateOrderStatus(orderNo, orderStatus);

        return ResponseEntity.created(URI.create("/" + response.getOrderNo())).body(response);
    }

    @GetMapping("/member-info")
    public ResponseEntity<?> getMemberAddress(Principal principal){
        Member member = memberService.findByEmail(principal.getName());
        return ResponseEntity.ok(member);
    }

    @GetMapping("/recent-order-no")
    public ResponseEntity<Long> getRecentOrderNo(){
        Long orderNo = orderService.getRecentOrder().getOrderNo();
        return ResponseEntity.ok(orderNo);
    }

    @PostMapping("/{orderNo}/canceled-order")
    public RedirectView cancelOrder(@PathVariable("orderNo") Long orderNo, String reason){
        CanceledOrder response = orderService.createCanceledOrder(orderNo, reason);
        orderService.updateOrderStatus(orderNo, OrderStatus.REFUND_REQUIRE);
        return new RedirectView("/order/" + orderNo);
    }

    @GetMapping("/product-option/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable("productId") Long productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PatchMapping("/address")
    public ResponseEntity<Void> updateAddress(@RequestBody AddressDto addressDto){
        orderService.updateAddress(addressDto);
        return ResponseEntity.ok().location(URI.create("/order" + addressDto.getOrderNo())).build();
    }

    @DeleteMapping("/canceled-order")
    public RedirectView deleteCanceledOrder(Long orderNo){
        orderService.deleteCanceledOrder(orderNo);
        return new RedirectView("/order/" + orderNo);
    }

    @GetMapping("/product-image")
    public ResponseEntity<String> productImage(Long productId){
        return ResponseEntity.ok(productService.getProductImageUrls(productId).getFirst());
    }

    @PostMapping("/{orderNo}/payment-fail")
    public ResponseEntity<Void> paymentFail(@PathVariable Long orderNo){
        orderService.updateOrderStatus(orderNo, OrderStatus.FAIL);
        log.warn("이거 호출됨");
        return ResponseEntity.ok().build();
    }
}
