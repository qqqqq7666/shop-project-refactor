package com.example.shop_project.point.entity;

import com.example.shop_project.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SaveReason saveReason;
    @Column(nullable = false)
    private Integer point;
    @Column(nullable = false)
    private Boolean isExhausted;
    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
