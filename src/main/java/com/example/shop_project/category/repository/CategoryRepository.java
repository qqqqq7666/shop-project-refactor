package com.example.shop_project.category.repository;

import com.example.shop_project.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDepth(int depth);
    Category findByCategoryNameAndDepth(String categoryName, int depth);
    Category findByCategoryId(Long categoryId);
    boolean existsByCategoryName(String categoryName);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory WHERE c.categoryId = :categoryId")
    Optional<Category> findByCategoryIdWithParent(@Param("categoryId") Long categoryId);


    // 메인 카테고리 조회 (부모가 없는 카테고리)
    List<Category> findByParentCategoryIsNull();

    // 특정 메인 카테고리에 해당하는 서브 카테고리 조회
    List<Category> findByParentCategory_CategoryId(Long parentId);

}
