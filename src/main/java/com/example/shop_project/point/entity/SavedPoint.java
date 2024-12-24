package com.example.shop_project.point.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long savedPointId;
    private Integer savedPoint;
    private SaveReason saveReason;
    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;
}
