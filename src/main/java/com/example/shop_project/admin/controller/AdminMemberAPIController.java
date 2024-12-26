package com.example.shop_project.admin.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/members")
public class AdminMemberAPIController {
	private final MemberRepository memberRepository;
	
	@PutMapping(value = "/role/{id}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
	public ResponseEntity<?> updateRole(@PathVariable("id") Long id, @RequestBody  Map<String, String> request) {
	    Optional<Member> optionalMember = memberRepository.findById(id);

	    Member member = optionalMember.get();
	    Role newRole = Role.valueOf(request.get("role")); 
	    member.setRole(newRole);
	    memberRepository.save(member);

	    return ResponseEntity.ok("권한이 변경되었습니다.");
	}
	
	// 회원 삭제 (withdraw 값 변경)
    @PutMapping(value = "/withdraw/{id}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public ResponseEntity<?> withdrawMember(@PathVariable("id") Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);

        Member member = optionalMember.get();
        if (member.getWithdraw()) {
            return ResponseEntity.badRequest().body("이미 탈퇴된 회원입니다.");
        }

        member.setWithdraw(true); 
        memberRepository.save(member);

        return ResponseEntity.ok("회원이 탈퇴 처리되었습니다.");
    }
}
