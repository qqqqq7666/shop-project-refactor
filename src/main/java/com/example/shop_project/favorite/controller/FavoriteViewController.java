package com.example.shop_project.favorite.controller;

import com.example.shop_project.favorite.service.FavoriteService;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteViewController {

    private final FavoriteService favoriteService;
    private final MemberService memberService;

    // 찜 목록 조회
    @GetMapping
    public String favoriteListPage(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                   @RequestParam(value = "size", required = false, defaultValue = "8") int size,
                                   Principal principal, Model model) {
        Long memberId = getMemberIdFromPrincipal(principal);

        // 찜 목록을 페이지네이션으로 가져오기
        Page<ProductResponseDto> favoriteProductPage = favoriteService.getFavoriteProducts(memberId, page, size);

        // 페이지네이션 블록 설정
        int blockSize = 5; // 페이지 블록 크기 설정
        int totalPages = favoriteProductPage.getTotalPages();

        int currentBlock = page / blockSize; // 현재 블록 계산
        int startPage = currentBlock * blockSize + 1; // 시작 페이지 계산
        int endPage = Math.min(startPage + blockSize - 1, totalPages); // 끝 페이지 계산

        model.addAttribute("favoriteProductPage", favoriteProductPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "favorite/favoriteList";
    }

    private Long getMemberIdFromPrincipal(Principal principal) {
        // Principal에서 사용자 ID 가져오는 로직 구현
        String email = principal.getName(); // 로그인된 사용자의 이메일
        Member member = memberService.findByEmail(email);
        return member.getId();
    }
}