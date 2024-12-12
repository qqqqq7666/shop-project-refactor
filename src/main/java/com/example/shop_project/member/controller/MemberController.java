package com.example.shop_project.member.controller;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import lombok.RequiredArgsConstructor;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
@Controller
public class MemberController {
	private final MemberService memberService;
	private final MemberRepository memberRepository; 

	@GetMapping("/join")
	public String Join() {
		return "join";
	}
	
	@PostMapping("/join")
	public String saveMember(@Validated @ModelAttribute MemberRequestDTO memberRequestDTO,
				            @RequestParam("confirmPassword") String confirmPassword,
				            Model model, BindingResult bindingResult) {
		// 이메일 형식 검사 (@하나 포함, @뒤에 .하나 포함)
		String emailRegex = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$";
		if (!memberRequestDTO.getEmail().matches(emailRegex)) {
			model.addAttribute("error", "올바른 이메일 형식이 아닙니다.");
			return "join";
		}
		
		// 패스워드 형식 검사 (최소 8글자, 대소문자, 숫자, 특수문자 최소 하나씩 포함)
		String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		if (!memberRequestDTO.getPassword().matches(passwordRegex)) {
			model.addAttribute("error", "비밀번호는 최소 8글자이며 대소문자, 숫자, 특수문자를 최소 하나씩 포함해야 합니다.");
			return "join";
		}
		
		// 비밀번호 불일치
		if (!memberRequestDTO.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "join";
        }
		
		if (bindingResult.hasErrors()) {
	        model.addAttribute("error", "모든 필드를 올바르게 입력해주세요.");
	        return "join"; // 다시 회원가입 페이지로
	    }
		
		try {
			memberService.Join(memberRequestDTO);		
			return "redirect:/login";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
            return "join";
		}
	}
	
	@GetMapping("/login")
	public String Login() {
		return "login";
	}
	
	@GetMapping("/mypage")
	public String Login(Model model, Principal principal) {
		String email = principal.getName();
        Member member = memberService.findByEmail(email);
        model.addAttribute("member", member);
		return "mypage";
	}
	
	@GetMapping("/mypage/edit")
	public String Login(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		String email = userDetails.getUsername();
        Member member = memberService.findByEmail(email);
        
        model.addAttribute("member", member);
        return "editProfile";
	}
	
	@PostMapping("/mypage/edit")
    public String updateMemberInfo(@AuthenticationPrincipal UserDetails userDetails,
                                    @ModelAttribute MemberRequestDTO memberRequestDTO,
                                    Model model) {
        String email = userDetails.getUsername();
        Member member = memberService.findByEmail(email);
  
        // 비밀번호 확인 체크
        if (!memberRequestDTO.getPassword().equals(memberRequestDTO.getConfirmPassword())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "editMember";
        }

        // 회원 정보를 업데이트
        memberService.updateMember(member, memberRequestDTO);

        return "redirect:/mypage";  // 수정된 후 마이페이지로 리다이렉트
    }
	
	@PostMapping("/mypage/withdraw")
    public ResponseEntity<String> withdrawAccount(Authentication authentication) {
        String username = authentication.getName();
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다"));

        member.setWithdraw(true);
        memberRepository.save(member);

        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
