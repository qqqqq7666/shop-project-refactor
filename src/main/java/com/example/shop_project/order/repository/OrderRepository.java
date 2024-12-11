package com.example.shop_project.order.repository;

import com.example.shop_project.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//    List<Order> findAllByMemberId(Long memberId);
    Order save(Order order);
    Optional<Order> findByOrderNo(Long orderNo);
    void deleteByOrderNo(Long orderNo);
}
