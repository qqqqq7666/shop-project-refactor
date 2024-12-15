package com.example.shop_project.product.repository;

import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productImageRepository extends JpaRepository<ProductImage, Long> {

}
