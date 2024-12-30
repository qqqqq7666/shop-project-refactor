package com.example.shop_project.member.repository;

import java.util.List;
import java.util.Optional;

import com.example.shop_project.member.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);
	Optional<Member> findByNickname(String nickname);
	List<Member> findAllByMembership(Membership membership);

	boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    
    long countByRole(Role role);
}
