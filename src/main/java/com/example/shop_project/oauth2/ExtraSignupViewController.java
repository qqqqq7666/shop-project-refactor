package com.example.shop_project.oauth2;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ExtraSignupViewController {

    private final MemberRepository memberRepository;

    /**
     * 1) GET /signup/extra
     *    - 구글로 로그인은 됐지만, DB에는 아직 없다 (혹은 업데이트 전)
     *    - 사용자에게 주소, 전화번호 등 추가 정보 입력 폼
     */
    @GetMapping("/signup/extra")
    public String extraForm(
            @AuthenticationPrincipal OAuth2User principal,
            Model model
    ) {
        if (principal == null) {
            // 인증이 안 된 상태 (구글 로그인 실패 등)
            return "redirect:/login?error=noPrincipal";
        }
        
        // 구글에서 가져온 기본 정보
        String email = principal.getAttribute("email");
        String name  = principal.getAttribute("name");
        
        // 뷰에 표시하기 위해 model에 담기
        model.addAttribute("email", email);
        model.addAttribute("name", name);

        // ※ extraSignupForm.html 은
        //   src/main/resources/templates/member/extraSignupForm.html 에 위치한다고 가정
        //   (Controller에서 return "member/extraSignupForm")
        return "member/extraSignupForm";
    }

    /**
     * 2) POST /signup/extra
     *    - 사용자가 폼에서 입력한 주소, 전화번호, 우편번호 등 필수 정보를 받아
     *      DB에 최종 회원으로 insert
     */
    @PostMapping("/signup/extra")
    public String extraSubmit(
        @AuthenticationPrincipal OAuth2User principal,
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
        
        // (1) DB에 유저가 이미 있는지 체크
        //     - "없으면 새로 저장", "있으면 에러" or 업데이트 로직
        //     여기서는 "항상 새로 만든다" 가정
        //     (원한다면 findByEmail로 중복 체크 후, 있으면 .update() 로직)
        
        Member member = Member.builder()
                .email(email)
                .name(name)
                .phone(phone)                  // 필수: phone
                .address(address)              // 필수: address
                .addressDetail(addressDetail)
                .postNo(postNo)
                .password("OAUTH2_USER")       // << 여기서 임시 비번
                .role(Role.USER)               // ex) 기본 권한
                .build();
        
        // DB insert
        memberRepository.save(member);

        // 가입 완료 후 /home 등으로 이동
        return "redirect:/home";
    }
}
