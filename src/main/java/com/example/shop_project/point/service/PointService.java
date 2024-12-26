package com.example.shop_project.point.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.mapper.PointMapper;
import com.example.shop_project.point.repository.PointRepository;
import com.example.shop_project.point.repository.SavedPointRepository;
import com.example.shop_project.point.repository.UsedPointRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private SavedPointRepository savedPointRepository;
    @Autowired
    private UsedPointRepository usedPointRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PointMapper pointMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ObjectMapper objectMapper;

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

//    public PointDto createSavedPoint(SavedPointRequestDto requestDto){
//        Point point = findPointByEmail(requestDto.getEmail());
//        SavedPoint savedPoint = pointMapper.toEntity(requestDto);
//        try {
//            log.warn("savedPoint = {}", objectMapper.writeValueAsString(savedPoint));
//        } catch (Exception e){
//            e.getStackTrace();
//        }
//        point.savePoint(pointMapper.toEntity(requestDto));
//        return pointMapper.toDto(pointRepository.save(point));
//    }
//
//    public PointDto createUsedPoint(UsedPointRequestDto requestDto){
//        try {
//            log.warn("usedPointDto = {}", objectMapper.writeValueAsString(requestDto));
//        } catch (Exception e){
//            e.getStackTrace();
//        }
//        Point point = findPointByEmail(requestDto.getEmail());
//        point.usePoint(pointMapper.toEntity(requestDto, orderRepository));
//        return pointMapper.toDto(pointRepository.save(point));
//    }

    public PointDto createPointHistory(PointHistoryRequestDto pointHistoryRequestDto){
        Point point = findPointByEmail(pointHistoryRequestDto.getEmail());
        point.updatePoint(pointMapper.toEntity(pointHistoryRequestDto, orderRepository));

        return pointMapper.toDto(point);
    }

    public Point findPointByEmail(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return pointRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }
}
