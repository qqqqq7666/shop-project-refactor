package com.example.shop_project.order.entity;

import com.example.shop_project.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@DynamicInsert
public class CanceledOrder extends BaseEntity {
    @Id
    @Column(name = "order_no", nullable = false)
    private Long orderNo;
    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_no")
    private Order order;
    @Column(nullable = false, length = 100)
    private String reason;
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isConfirmed;
}
