package com.example.shop_project.category.repository;

import com.example.shop_project.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDepth(int depth);
    Category findByCategoryName(String categoryName);
}
