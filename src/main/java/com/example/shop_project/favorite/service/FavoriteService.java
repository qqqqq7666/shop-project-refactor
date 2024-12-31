package com.example.shop_project.favorite.service;

import com.example.shop_project.favorite.entity.Favorite;
import com.example.shop_project.favorite.repository.FavoriteRepository;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.ProductImage;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    // 상품 찜하기
    @Transactional
    public void addFavorite(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (favoriteRepository.findByMemberAndProduct(member, product).isPresent()) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }

        Favorite favorite = new Favorite();
        favorite.setMember(member);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
    }

    // 찜 해제
    @Transactional
    public void removeFavorite(Long memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        favoriteRepository.deleteByMemberAndProduct(member, product);
    }

    // 사용자의 찜 목록 조회
    @Transactional
    public Page<ProductResponseDto> getFavoriteProducts(Long memberId, int page, int size) {
        // 페이징 설정
        Pageable pageable = PageRequest.of(page, size);

        // 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자의 찜한 상품 목록 페이징 조회
        Page<Favorite> favoritePage = favoriteRepository.findByMember(member, pageable);

        // Favorite -> Product -> ProductResponseDto 변환
        return favoritePage.map(favorite -> {
            Product product = favorite.getProduct();
            return ProductResponseDto.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .price(product.getPrice())
                    .nickname(product.getNickname())
                    .viewCount(product.getViewCount())
                    .salesCount(product.getSalesCount())
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .imageUrls(product.getImages().stream()
                            .map(ProductImage::getImageUrl)
                            .toList())
                    .build();
        });
    }

}
