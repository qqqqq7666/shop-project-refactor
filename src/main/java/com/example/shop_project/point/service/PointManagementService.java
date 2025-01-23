package com.example.shop_project.point.service;

import com.example.shop_project.member.Membership;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.PointDto;
import com.example.shop_project.point.dto.SavedPointRequestDto;
import com.example.shop_project.point.dto.UsedPointRequestDto;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.mapper.PointMapper;
import com.example.shop_project.point.repository.PointRepository;
import com.example.shop_project.point.repository.SavedPointRepository;
import com.example.shop_project.point.repository.UsedPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointManagementService {
    private final PointRepository pointRepository;
    private final SavedPointRepository savedPointRepository;
    private final UsedPointRepository usedPointRepository;
    private final MemberRepository memberRepository;
    private final PointMapper pointMapper;
    private final OrderRepository orderRepository;

    private Point findPointByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return pointRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }

    public void createPointByEmail(String email) {
        Point point = Point.builder()
                .member(memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")))
                .build();
        pointRepository.save(point);
    }

    public PointDto createSavedPoint(SavedPointRequestDto requestDto) {
        Point point = findPointByEmail(requestDto.getEmail());

        point.savePoint(pointMapper.toEntity(requestDto));
        return pointMapper.toDto(pointRepository.save(point));
    }

    public PointDto createUsedPoint(UsedPointRequestDto requestDto) {
        Point point = findPointByEmail(requestDto.getEmail());
        point.usePoint(pointMapper.toEntity(requestDto, orderRepository));
        return pointMapper.toDto(pointRepository.save(point));
    }

    @Transactional
    public void monthlyPointSave() {
        memberRepository.findAllByMembership(Membership.DIAMOND).forEach(member -> {
            findPointByEmail(member.getEmail()).savePoint(SavedPoint.builder()
                    .savedPoint(50000)
                    .saveReason("매월 맴버십 DIAMOND 등급 혜택")
                    .build());
        });
        memberRepository.findAllByMembership(Membership.GOLD).forEach(member -> {
            findPointByEmail(member.getEmail()).savePoint(SavedPoint.builder()
                    .savedPoint(10000)
                    .saveReason("매월 맴버십 GOLD 등급 혜택")
                    .build());
        });
        memberRepository.findAllByMembership(Membership.SILVER).forEach(member -> {
            findPointByEmail(member.getEmail()).savePoint(SavedPoint.builder()
                    .savedPoint(5000)
                    .saveReason("매월 맴버십 SILVER 등급 혜택")
                    .build());
        });
    }

    public void cancelSavedPoint(Long savedPointId) {
        SavedPoint savedPoint = savedPointRepository.findById(savedPointId).orElseThrow();
        savedPoint.getPoint().rollbackBalance(savedPoint.getSavedPoint());
        savedPointRepository.delete(savedPoint);
    }

    public void cancelUsedPoint(Long usedPointId) {
        UsedPoint usedPoint = usedPointRepository.findById(usedPointId).orElseThrow();
        usedPoint.getPoint().rollbackBalance(-usedPoint.getAmount());
        usedPointRepository.delete(usedPoint);
    }
}
