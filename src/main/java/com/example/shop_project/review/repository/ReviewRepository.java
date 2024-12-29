package com.example.shop_project.review.repository;

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
//    List<Review> findByProductId(Long productId);
    Page<Review> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT ROUND(AVG(r.stars), 1) FROM Review r WHERE r.productId = :productId")
    Double averageStarsByProductId(@Param("productId") Long productId);

}
