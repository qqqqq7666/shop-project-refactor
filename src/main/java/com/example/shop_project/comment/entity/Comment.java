package com.example.shop_project.comment.entity;

import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry; // 문의

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 작성자

    @Column(nullable = false)
    private String content; // 댓글 내용

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 댓글 작성 시간

}