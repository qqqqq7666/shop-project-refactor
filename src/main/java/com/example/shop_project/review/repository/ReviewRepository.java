package com.example.shop_project.review.repository;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 상품별 리뷰 조회
    Page<Review> findByProductId(Long productId, Pageable pageable);
    // 회원별 리뷰 조회
    Page<Review> findByMember(Member member, Pageable pageable);
    // 회원별 리뷰수 조회
    int countByMember(Member member);
    // OrderDetail에 해당하는 리뷰 존재 여부
    boolean existsByOrderDetail(OrderDetail orderDetail);

    // 별점 평균 조회
    @Query("SELECT ROUND(AVG(r.stars), 1) FROM Review r WHERE r.productId = :productId")
    Double averageStarsByProductId(@Param("productId") Long productId);

}
