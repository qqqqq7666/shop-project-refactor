package com.example.shop_project.order.service;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.PaymentDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.Payment;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.order.repository.PaymentRepository;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.ProductOption;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
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
    public Payment getPaymentByOrderNo(Long orderNo){
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        return paymentRepository.findByOrder(order).orElseThrow(() -> new IllegalArgumentException("주문에 대한 결제가 존재하지 않습니다."));
    }

    @Transactional
    public void decreaseProductStock(List<OrderDetailDto> orderDetailDtoList){
        orderDetailDtoList.forEach(orderDetailDto -> {
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

            for(ProductOption option : product.getOptions()){
                if(option.getSize() == orderDetailDto.getSize() && Objects.equals(option.getColor(), orderDetailDto.getColor())) {
                    int updatedStock = (int) (option.getStockQuantity() - orderDetailDto.getQuantity());
                    if(updatedStock < 0)
                        throw new IllegalStateException("상품 재고가 부족합니다.");
                    option.setStockQuantity(updatedStock);
                    break;
                }
            }
            productRepository.save(product);
        });
    }

    @Transactional
    public void productStockRollback(List<OrderDetailDto> orderDetailDtoList){
        orderDetailDtoList.forEach(orderDetailDto -> {
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다."));

            for(ProductOption option : product.getOptions()){
                if(option.getSize() == orderDetailDto.getSize() && Objects.equals(option.getColor(), orderDetailDto.getColor())) {
                    int updatedStock = (int) (option.getStockQuantity() + orderDetailDto.getQuantity());
                    option.setStockQuantity(updatedStock);
                    break;
                }
            }
            productRepository.save(product);
        });
    }
}
