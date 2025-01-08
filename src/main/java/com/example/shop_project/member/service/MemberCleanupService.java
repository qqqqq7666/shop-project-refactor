package com.example.shop_project.member.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shop_project.jwt.JwtProviderImpl;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberCleanupService {
	private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시에 실행
    public void deleteOldWithdrawnMembers() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Member> oldWithdrawnMembers = memberRepository.findByWithdrawTrueAndWithdrawDateBefore(thirtyDaysAgo);
        memberRepository.deleteAll(oldWithdrawnMembers);
    }
}
