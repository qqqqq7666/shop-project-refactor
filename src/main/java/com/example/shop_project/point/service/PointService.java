package com.example.shop_project.point.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.PointDto;
import com.example.shop_project.point.dto.PointHistoryDto;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.mapper.PointMapper;
import com.example.shop_project.point.repository.PointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PointMapper pointMapper;
    @Autowired
    private OrderRepository orderRepository;

    // 회원가입할 때 같이 생성
    public void createPoint(Long memberId){
        Point point = Point.builder()
                .member(memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")))
                .build();
        pointRepository.save(point);
    }

    public PointDto getPointByMember(String email){
        Point point = findPointByEmail(email);
        return pointMapper.toDto(point);
    }

    @Transactional
    public PointDto createPointHistory(PointHistoryDto pointHistoryDto){
        Point point = findPointByEmail(pointHistoryDto.getSavedPointRequestDto().getEmail());
        point.updatePoint(pointMapper.toEntity(pointHistoryDto.getSavedPointRequestDto(), orderRepository));

        if(pointHistoryDto.getIsPaidWithPoint())
            point.updatePoint(pointMapper.toEntity(pointHistoryDto.getUsedPointRequestDto(), orderRepository));

        return pointMapper.toDto(pointRepository.save(point));
    }

    public Point findPointByEmail(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return pointRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }
}
