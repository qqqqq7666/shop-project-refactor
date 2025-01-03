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

    // cron: 초(0-59) 분(0-59) 시(0-23) 일(1-31) 월(1-12) 요일(0-7 or SUN-SAT)
    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시에 실행
    public void deleteOldWithdrawnMembers() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Member> oldWithdrawnMembers = memberRepository.findByWithdrawTrueAndWithdrawDateBefore(thirtyDaysAgo);
        memberRepository.deleteAll(oldWithdrawnMembers);
    }
}
