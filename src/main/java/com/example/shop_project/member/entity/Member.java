package com.example.shop_project.member.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.processing.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
	
	// 주소, 우편번호 후순위
//	@Column(name = "post_no", nullable = false)
//	private String postNo;
//	
//	@Column(name = "address_detail", nullable = false)
//	private String addressDetail;
	
	@Column(name = "nickname", nullable = false)
	private String nickname;
	
	@Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
	
	// 후에 들어갈 컬럼들
	// 권한, 등급(멤버십), 프로필 사진 url
	
	@PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
