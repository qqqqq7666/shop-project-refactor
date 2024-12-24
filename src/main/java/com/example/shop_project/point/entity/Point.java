package com.example.shop_project.point.entity;

import com.example.shop_project.BaseEntity;
import com.example.shop_project.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;
    @Column(nullable = false)
    private Integer balance;
    private Integer totalSavedPoint;
    private Integer totalUsedPoint;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
