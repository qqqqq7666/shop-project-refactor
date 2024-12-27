package com.example.shop_project.point.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SavedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long savedPointId;
    @Column(nullable = false)
    private Integer savedPoint;
    @Column(nullable = false)
    private String saveReason;
    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    public void assignPointToCreate(Point point){
        this.point = point;
    }
}
