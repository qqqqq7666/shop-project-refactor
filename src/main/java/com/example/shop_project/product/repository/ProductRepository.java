package com.example.shop_project.product.repository;

import com.example.shop_project.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByProductNameContaining(String productName, Pageable pageable);
    List<Product> findAll(Sort sort);
    List<Product> findByProductNameContaining(String keyword);

    List<Product> findAllByProductNameContaining(String productName);


    @Modifying
    @Query(value = "UPDATE Product p SET p.viewCount = p.viewCount + :increment WHERE p.productId = :productId")
    void incrementViewCount(@Param("productId") Long productId, @Param("increment") Integer increment);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    // 판매량 top5가져오기
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images i WHERE i.sortOrder = 1 ORDER BY p.salesCount DESC LIMIT 4")
    List<Product> findTop5BestSellersWithFirstImage();

    Page<Product> findByCategoryIdAndProductNameContaining(Long categoryId, String search, Pageable pageable);
}
