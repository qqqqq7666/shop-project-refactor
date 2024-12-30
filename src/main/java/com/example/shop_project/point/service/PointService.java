package com.example.shop_project.point.service;

import com.example.shop_project.member.Membership;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.mapper.PointMapper;
import com.example.shop_project.point.repository.PointRepository;
import com.example.shop_project.point.repository.SavedPointRepository;
import com.example.shop_project.point.repository.UsedPointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private UsedPointRepository usedPointRepository;
    @Autowired
    private SavedPointRepository savedPointRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PointMapper pointMapper;
    @Autowired
    private OrderRepository orderRepository;

    // 회원가입할 때 같이 생성
    public void createPointByEmail(String email) {
        Point point = Point.builder()
                .member(memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")))
                .build();
        pointRepository.save(point);
    }

    public PointDto getPointByMember(String email) {
        Point point = findPointByEmail(email);
        return pointMapper.toDto(point);
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

    public Point findPointByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return pointRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }

    public UsedPointResponseDto getUsedPointByOrderNo(Long orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
        UsedPoint usedPoint = usedPointRepository.findByOrder(order).orElseGet(() -> UsedPoint.builder().amount(0).build());

        return pointMapper.toResponseDto(usedPoint);
    }

    public List<PointHistoryDto> getPointList(String email, String category) {
        Point point = findPointByEmail(email);
        List<PointHistoryDto> savedPointHistoryDtoList = new ArrayList<>();
        List<PointHistoryDto> usedPointHistoryDtoList = new ArrayList<>();
        List<SavedPoint> savedPointList = savedPointRepository.findAllByPoint(point);
        List<UsedPoint> usedPointList = usedPointRepository.findAllByPoint(point);
        savedPointList.forEach(savedPoint -> {
            savedPointHistoryDtoList.add(PointHistoryDto.builder()
                    .createdDate(savedPoint.getCreatedDate())
                    .amount(savedPoint.getSavedPoint())
                    .reason(savedPoint.getSaveReason())
                    .isUsed(false)
                    .build());
        });
        usedPointList.forEach(usedPoint -> {
            String reason = usedPoint.getOrder().getOrderDetailList().getFirst().getProduct().getProductName();
            if (usedPoint.getOrder().getOrderDetailList().size() > 1)
                reason += " 외 " + (usedPoint.getOrder().getOrderDetailList().size() - 1) + "개";
            usedPointHistoryDtoList.add(PointHistoryDto.builder()
                    .amount(usedPoint.getAmount())
                    .createdDate(usedPoint.getCreatedDate())
                    .order(usedPoint.getOrder())
                    .reason(reason)
                    .isUsed(true)
                    .build());
        });
        if (category.equals("all")) {
            List<PointHistoryDto> pointHistoryDtoList = new ArrayList<>();
            pointHistoryDtoList.addAll(savedPointHistoryDtoList);
            pointHistoryDtoList.addAll(usedPointHistoryDtoList);
            pointHistoryDtoList.sort(Comparator.comparing(PointHistoryDto::getCreatedDate).reversed());
            return pointHistoryDtoList;
        }

        if (category.equals("save"))
            return savedPointHistoryDtoList.reversed();
        if (category.equals("use"))
            return usedPointHistoryDtoList.reversed();

        return null;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
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

    public Integer getTotalSavedPoint(String email) {
        Integer totalSavedPoint = savedPointRepository.findTotalSavedPoint(findPointByEmail(email));
        if (totalSavedPoint == null)
            return 0;
        return totalSavedPoint;
    }

    public List<Point> getTotalPointList() {
        return pointRepository.findAll();
    }

    // admin
    public List<PointHistoryDto> getTotalPointHistory(Long pointId, String category) {
        Point point = pointRepository.findById(pointId).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
        List<PointHistoryDto> savedPointHistoryDtoList = new ArrayList<>();
        List<PointHistoryDto> usedPointHistoryDtoList = new ArrayList<>();
        List<SavedPoint> savedPointList = savedPointRepository.findAllByPoint(point);
        List<UsedPoint> usedPointList = usedPointRepository.findAllByPoint(point);
        savedPointList.forEach(savedPoint -> {
            savedPointHistoryDtoList.add(PointHistoryDto.builder()
                    .createdDate(savedPoint.getCreatedDate())
                    .amount(savedPoint.getSavedPoint())
                    .reason(savedPoint.getSaveReason())
                    .isUsed(false)
                    .pointId(savedPoint.getSavedPointId())
                    .build());
        });
        usedPointList.forEach(usedPoint -> {
            String reason = usedPoint.getOrder().getOrderDetailList().getFirst().getProduct().getProductName();
            if (usedPoint.getOrder().getOrderDetailList().size() > 1)
                reason += " 외 " + (usedPoint.getOrder().getOrderDetailList().size() - 1) + "개";
            usedPointHistoryDtoList.add(PointHistoryDto.builder()
                    .amount(usedPoint.getAmount())
                    .createdDate(usedPoint.getCreatedDate())
                    .order(usedPoint.getOrder())
                    .reason(reason)
                    .pointId(usedPoint.getUsedPointId())
                    .isUsed(true)
                    .build());
        });
        if (category.equals("all")) {
            List<PointHistoryDto> pointHistoryDtoList = new ArrayList<>();
            pointHistoryDtoList.addAll(savedPointHistoryDtoList);
            pointHistoryDtoList.addAll(usedPointHistoryDtoList);
            pointHistoryDtoList.sort(Comparator.comparing(PointHistoryDto::getCreatedDate).reversed());
            return pointHistoryDtoList;
        }

        if (category.equals("save"))
            return savedPointHistoryDtoList.reversed();
        if (category.equals("use"))
            return usedPointHistoryDtoList.reversed();

        return null;
    }

    public Point getPointById(Long pointId) {
        return pointRepository.findById(pointId).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }

    public Member getMemberByPointId(Long pointId) {
        return pointRepository.findById(pointId).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."))
                .getMember();
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
