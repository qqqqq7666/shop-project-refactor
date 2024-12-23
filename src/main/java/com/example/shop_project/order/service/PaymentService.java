package com.example.shop_project.order.service;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.PaymentDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.Payment;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.order.repository.PaymentRepository;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.ProductOption;
import com.example.shop_project.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private ProductRepository productRepository;

    public Payment createPayment(PaymentDto paymentDto){
        Payment newPayment = orderMapper.toEntity(paymentDto);
        newPayment.assignOrderToCreate(orderRepository.findFirstByOrderByOrderNoDesc().orElseThrow(() -> new NoSuchElementException("주문이 존재하지 않습니다.")));
        return paymentRepository.save(newPayment);
    }

    @Transactional
    public Payment getPaymentByOrderNo(Long orderNo){
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        return paymentRepository.findByOrder(order).orElseThrow();
    }

    public void productStockUpdate(List<OrderDetailDto> orderDetailDtoList){
        orderDetailDtoList.forEach(orderDetailDto -> {
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

            for(ProductOption option : product.getOptions()){
                if(option.getSize() == orderDetailDto.getSize() && Objects.equals(option.getColor(), orderDetailDto.getColor())) {
                    option.setStockQuantity((int) (option.getStockQuantity() - orderDetailDto.getQuantity()));
                    break;
                }
            }

            productRepository.save(product);
        });
    }
}
