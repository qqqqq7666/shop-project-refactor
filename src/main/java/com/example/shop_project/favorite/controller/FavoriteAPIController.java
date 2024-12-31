package com.example.shop_project.favorite.controller;

import com.example.shop_project.favorite.service.FavoriteService;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteAPIController {
    private final FavoriteService favoriteService;
    private final MemberService memberService;

    // 상품 찜하기
    @PostMapping("/{productId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long productId, Principal principal) {
        try {
            Long memberId = getMemberIdFromPrincipal(principal); // Principal에서 사용자 ID 가져오기
            favoriteService.addFavorite(memberId, productId);
            return ResponseEntity.ok("상품이 찜 목록에 추가되었습니다.");
        } catch (Exception e) {
            // 로깅 추가
            log.error("### Failed to update favorites for productId: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("찜 목록 업데이트 실패");
        }
    }

    // 찜 해제
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long productId, Principal principal) {
        Long memberId = getMemberIdFromPrincipal(principal);
        favoriteService.removeFavorite(memberId, productId);
        return ResponseEntity.ok("찜 목록에서 상품이 제거되었습니다.");
    }

    private Long getMemberIdFromPrincipal(Principal principal) {
        // Principal에서 사용자 ID 가져오는 로직 구현
        String email = principal.getName(); // 로그인된 사용자의 이메일
        Member member = memberService.findByEmail(email);
        return member.getId();
    }
}