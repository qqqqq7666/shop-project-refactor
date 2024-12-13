package com.example.shop_project.order.repository;

import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrder(Order order);
    OrderDetail save(OrderDetail orderDetail);
    Optional<OrderDetail> findById(Long orderDetailId);
    void deleteByOrder(Order order);
}
