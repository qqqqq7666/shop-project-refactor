package com.example.shop_project;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {
	@GetMapping("/join")
	public String Join() {
		
		return "join";
	}
}
