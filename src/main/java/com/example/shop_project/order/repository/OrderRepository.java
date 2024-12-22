package com.example.shop_project.order.repository;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//    List<Order> findAllByMemberId(Long memberId);
    Page<Order> findAllByMemberAndOrderStatusNotOrderByOrderNoDesc(Member member, Pageable pageable, OrderStatus orderStatus);
    Order save(Order order);
    Optional<Order> findByOrderNo(Long orderNo);
    void deleteByOrderNo(Long orderNo);
    List<Order> findAllByOrderByOrderNoDesc();
    Optional<Order> findFirstByOrderByOrderNoDesc();
}
