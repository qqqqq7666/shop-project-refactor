package com.example.shop_project.point.entity;

import com.example.shop_project.order.entity.Order;
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
public class UsedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usedPointId;
    @Column(nullable = false)
    private Integer amount;
    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @OneToOne
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    public void assignPointToCreate(Point point){
        this.point = point;
    }
}
