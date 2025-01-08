package com.example.shop_project.favorite.repository;

import com.example.shop_project.favorite.entity.Favorite;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByMember(Member member);
    Optional<Favorite> findByMemberAndProduct(Member member, Product product);
    void deleteByMemberAndProduct(Member member, Product product);

    @Query("SELECT f FROM Favorite f WHERE f.member = :member")
    Page<Favorite> findByMember(@Param("member") Member member, Pageable pageable);
}

