package com.example.shop_project.oauth2;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shop_project.member.Provider;
import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class ExtraSignupViewController {

    private final MemberRepository memberRepository;

    @GetMapping("/confirm")
    public String confirmSignup(
            @AuthenticationPrincipal OAuth2User principal,
            Model model
    ) {
        if (principal == null) {
            return "redirect:/login?error=noPrincipal";
        }
        String email = principal.getAttribute("email");
        
        // DB 조회
        boolean exists = memberRepository.findByEmail(email).isPresent();
        model.addAttribute("exists", exists);
        model.addAttribute("email", email);
        return "member/confirmSignup"; 
    }
    
    @GetMapping("/extra")
    public String extraForm(@AuthenticationPrincipal OAuth2User principal,
                            Model model) {
        if (principal == null) {
            return "redirect:/login?error=noPrincipal";
        }
        // 이메일 가져오기
        String email = principal.getAttribute("email");
        // DB에서 조회
        Optional<Member> existingUser = memberRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // 이미 등록된 유저 -> 추가 정보 입력 필요 X
            return "redirect:/"; 
        }

        // 없으면 => 새로 가입해야 하므로 추가 폼 보여주기
        String name = principal.getAttribute("name");
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        return "member/extraSignupForm";
    }

    /*
	- 사용자가 폼에서 입력한 주소, 전화번호, 우편번호 등 필수 정보를 받아
	DB에 최종 회원으로 insert
     */
    @PostMapping("/extra")
    public String extraSubmit(
        @AuthenticationPrincipal OAuth2User principal,
        @RequestParam(name="nickname") String nickname,
        @RequestParam(name="phone") String phone,
        @RequestParam(name="address") String address,
        @RequestParam(name="addressDetail") String addressDetail,
        @RequestParam(name="postNo") String postNo
    ) {
        if (principal == null) {
            return "redirect:/login?error=noPrincipal";
        }

        // 구글에서 이미 받아온 정보
        String email = principal.getAttribute("email");
        String name  = principal.getAttribute("name");
        // DB에 유저가 이미 있는지 체크
        // "없으면 새로 저장", "있으면 에러" or 업데이트 로직
        // 여기서는 "항상 새로 만든다" 가정
        // (원한다면 findByEmail로 중복 체크 후, 있으면 .update() 로직)
        
        Member member = Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .phone(phone)                  // 필수: phone
                .address(address)              // 필수: address
                .addressDetail(addressDetail)
                .postNo(postNo)
                .password("OAUTH2_USER")       // << 여기서 임시 비번
                .role(Role.USER)               // ex) 기본 권한
                .provider(Provider.GOOGLE)
                .build();
        
        // DB insert
        memberRepository.save(member);

        // 가입 완료 후 /으로 이동
        return "redirect:/";
    }
}
