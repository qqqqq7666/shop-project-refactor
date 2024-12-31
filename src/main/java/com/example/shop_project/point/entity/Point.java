package com.example.shop_project.point.entity;

import com.example.shop_project.BaseEntity;
import com.example.shop_project.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@DynamicInsert
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer balance;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedPoint> savedPointList = new ArrayList<>();

    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsedPoint> usedPointList = new ArrayList<>();

    public void usePoint(UsedPoint usedPoint){
        usedPoint.assignPointToCreate(this);
        usedPointList.add(usedPoint);
        balance -= usedPoint.getAmount();
    }

    public void savePoint(SavedPoint savedPoint){
        savedPoint.assignPointToCreate(this);
        savedPointList.add(savedPoint);
        balance += savedPoint.getSavedPoint();
    }

    public void rollbackBalance(Integer amount){
        balance -= amount;
    }
}
