package com.example.shop_project.order.repository;

import com.example.shop_project.order.entity.CanceledOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanceledOrderRepository extends JpaRepository<CanceledOrder, Long> {
}
