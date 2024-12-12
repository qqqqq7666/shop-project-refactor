package com.example.shop_project.member.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Controller
public class MemberController {
	private final MemberService memberService;
	
	
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
}
