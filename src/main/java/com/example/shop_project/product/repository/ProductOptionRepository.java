package com.example.shop_project.product.repository;

import com.example.shop_project.product.entity.ProductOption;
import com.example.shop_project.product.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    @Query("SELECT po.stockQuantity FROM ProductOption po WHERE po.product.id = :productId AND po.size = :size AND po.color = :color")
    Optional<Integer> findStockQuantityBySizeAndColor(@Param("productId") Long productId, @Param("size") Size size, @Param("color") String color);

    @Query("SELECT po FROM ProductOption po WHERE po.product.id = :productId AND po.color = :color")
    List<ProductOption> findByProductIdAndColor(@Param("productId") Long productId, @Param("color") String color);
}