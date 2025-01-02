package com.example.shop_project;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.ProductImage;
import com.example.shop_project.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {
	private final ProductRepository productRepository;
	
    @GetMapping
    public String home(Model model){
    	List<Product> bestSellers = productRepository.findTop5BestSellersWithFirstImage();
        // DTO로 변환
        List<ProductResponseDto> bestSellerDtos = bestSellers.stream().map(product -> {
            ProductResponseDto dto = new ProductResponseDto();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setPrice(product.getPrice());
            dto.setSalesCount(product.getSalesCount());
            dto.setDescription(product.getDescription());
            dto.setImageUrls(product.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getSortOrder)) // 정렬 보장
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("bestSellers", bestSellerDtos);
        return "home";
    }
}
