package com.example.shop_project.point.repository;

import com.example.shop_project.point.entity.UsedPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedPointRepository extends JpaRepository<UsedPoint, Long> {
}
