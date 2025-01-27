package com.example.shop_project.order.service;

import com.example.shop_project.order.entity.CanceledOrder;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.repository.CanceledOrderRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.repository.UsedPointRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderRefundService {
    private final CanceledOrderRepository canceledOrderRepository;
    private final UsedPointRepository usedPointRepository;
    private final OrderRepository orderRepository;

    public OrderRefundService(CanceledOrderRepository canceledOrderRepository, UsedPointRepository usedPointRepository, OrderRepository orderRepository) {
        this.canceledOrderRepository = canceledOrderRepository;
        this.usedPointRepository = usedPointRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void refund(Long orderNo){
        CanceledOrder canceledOrder = canceledOrderRepository.findById(orderNo).orElseThrow();
        Order order = canceledOrder.getOrder();
        canceledOrder.confirmRequire();
        if(order.getIsPaidWithPoint()){
            UsedPoint usedPoint = usedPointRepository.findByOrder(order).orElseThrow();
            usedPoint.getPoint().rollbackBalance(-usedPoint.getAmount());
            usedPointRepository.delete(usedPoint);
        }
        canceledOrderRepository.save(canceledOrder);
        order.updateStatus(OrderStatus.REFUND);
        orderRepository.save(order);
    }

    public CanceledOrder createCanceledOrder(Long orderNo, String reason) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        return canceledOrderRepository.save(CanceledOrder.builder()
                .order(order)
                .reason(reason)
                .build());
    }

    public CanceledOrder getCanceledOrder(Long orderNo){
        return canceledOrderRepository.findById(orderNo).orElseGet(() -> null);
    }

    @Transactional
    public void deleteCanceledOrder(Long orderNo){
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow();
        order.updateStatus(OrderStatus.NEW);
        orderRepository.save(order);
        CanceledOrder canceledOrder = canceledOrderRepository.findById(orderNo).orElseThrow();
        canceledOrderRepository.delete(canceledOrder);
    }
}
