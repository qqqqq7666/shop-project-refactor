package com.example.shop_project.point.repository;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByMember(Member member);
}
