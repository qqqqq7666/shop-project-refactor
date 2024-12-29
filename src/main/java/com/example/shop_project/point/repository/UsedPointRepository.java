package com.example.shop_project.point.repository;

import com.example.shop_project.order.entity.Order;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.UsedPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsedPointRepository extends JpaRepository<UsedPoint, Long> {
    Optional<UsedPoint> findByOrder(Order order);
    List<UsedPoint> findAllByPoint(Point point);
}
