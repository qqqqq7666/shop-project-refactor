package com.example.shop_project;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MemberController {
	private final MemberService memberService;
	
	
	@GetMapping("/join")
	public String Join() {
		
		return "join";
	}
	
	@PostMapping("/join")
	public String saveMember(@RequestParam("email") String email,
				            @RequestParam("nickname") String nickname,
				            @RequestParam("password") String password,
				            @RequestParam("confirmPassword") String confirmPassword,
				            @RequestParam("phone") String phone, Model model) {
		if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "join";
        }
		
		Member member = new Member();
		member.setEmail(email);
		member.setNickname(nickname);
		member.setPassword(password);
		member.setPhone(phone);
		
		memberService.Join(member);
		
		return "redirect:/login";
	}
}
