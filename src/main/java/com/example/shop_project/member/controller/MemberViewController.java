package com.example.shop_project.member.controller;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.inquiry.service.InquiryService;
import com.example.shop_project.point.service.PointService;
import com.example.shop_project.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.service.CustomMemberDetailService;
import com.example.shop_project.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
@Controller
public class MemberViewController {
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final PointService pointService;
	private final ReviewService reviewService;
	private final OrderService orderService;
	private final InquiryService inquiryService;

	@GetMapping("/join")
	public String Join() {
		return "member/join";
	}
	
	@PostMapping("/join")
	public String saveMember(@Validated @ModelAttribute MemberRequestDTO memberRequestDTO,
				            Model model, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
	        model.addAttribute("error", bindingResult.getFieldError().getDefaultMessage());
	        return "member/join";
	    }

		memberService.Join(memberRequestDTO);
		pointService.createPointByEmail(memberRequestDTO.getEmail());
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String Login() {
		return "member/login";
	}
	
	@GetMapping("/mypage")
	public String Login(Model model, Principal principal) {
		if (principal == null) {
			// 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
			return "redirect:/login";
		}
		
		String email = principal.getName();
        Member member = memberService.findByEmail(email);
		Long inquiryCount = inquiryService.getInquiryCountByMember(email);
        model.addAttribute("member", member);
		model.addAttribute("point", pointService.getPointByMember(email));
		model.addAttribute("orderCount", orderService.getOrderCountByEmail(email));
		model.addAttribute("reviewCount", reviewService.getReviewCountByMember(member));
		model.addAttribute("inquiryCount", inquiryCount);
		return "member/mypage";
	}
	
	@GetMapping("/mypage/edit")
	public String Login(Principal principal, Model model) {
	    if (principal == null) {
	        return "redirect:/login";
	    }

	    String email = principal.getName(); // 이메일 가져오기
	    Member member = memberService.findByEmail(email);
	    model.addAttribute("member", member);
	    return "member/editProfile";
	}
	
	@PostMapping("/mypage/edit")
	public String updateMemberInfo(Principal principal,
	                                @ModelAttribute MemberRequestDTO memberRequestDTO,
	                                Model model) {
	    String email = principal.getName();
	    Member member = memberService.findByEmail(email);

	    // 회원 정보를 업데이트
	    memberService.updateMember(member, memberRequestDTO);

	    return "redirect:/mypage";  // 수정된 후 마이페이지로 리다이렉트
	}

	
	@PostMapping(value = "/mypage/withdraw", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public ResponseEntity<String> withdrawAccount(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        String username = authentication.getName();
        Member member = memberRepository.findByEmail(username).orElseThrow();

        member.setWithdraw(true);
        memberRepository.save(member);
        
        SecurityContextHolder.clearContext(); // Spring Security 인증 정보 삭제
        request.getSession().invalidate();    // 세션 무효화

        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
	
    @GetMapping("/password-reset")
    public String showPasswordResetPage() {
        return "member/findPassword";
    }
    
    @PostMapping("/password-reset")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        Member member = memberService.findByEmail(email);

        if (member != null) {
            String tempPassword = generateTemporaryPassword();
            
            MemberRequestDTO memberDTO = new MemberRequestDTO();
            memberDTO.setPassword(tempPassword);
            memberDTO.setNickname(member.getNickname());
            memberDTO.setPhone(member.getPhone());
            memberDTO.setPostNo(member.getPostNo());
            memberDTO.setAddress(member.getAddress());
            memberDTO.setAddressDetail(member.getAddressDetail());

            memberService.updateMember(member, memberDTO);

            model.addAttribute("tempPassword", tempPassword);
        } else {
            model.addAttribute("error", "해당 이메일로 등록된 사용자가 없습니다.");
        }

        return "member/findPassword";
    }

    private String generateTemporaryPassword() {
        // 간단한 임시 비밀번호 생성
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
