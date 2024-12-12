package com.example.shop_project.member.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.processing.Pattern;

import com.example.shop_project.member.Membership;
import com.example.shop_project.member.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "phone", nullable = false)
	private String phone;
	
	@Column(name = "post_no", nullable = false)
	private String postNo;
	
	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "address_detail", nullable = false)
	private String addressDetail;
	
	@Column(name = "nickname", nullable = false)
	private String nickname;
	
	@Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
	
	// 프로필 사진 URL
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // 권한 (스프링 시큐리티와 연동)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // 회원 등급
    @Enumerated(EnumType.STRING)
    @Column(name = "membership")
    private Membership membership;
	
	@PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        // 기본값 설정
        if (this.role == null) 
            this.role = Role.USER;
        
        if (this.membership == null) 
            this.membership = Membership.BRONZE;
    }
	
	@PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
