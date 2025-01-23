package com.example.shop_project.point.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.point.dto.PointDto;
import com.example.shop_project.point.dto.PointHistoryDto;
import com.example.shop_project.point.dto.UsedPointResponseDto;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.mapper.PointMapper;
import com.example.shop_project.point.repository.PointRepository;
import com.example.shop_project.point.repository.SavedPointRepository;
import com.example.shop_project.point.repository.UsedPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PointRetrieveService {
    private final PointMapper pointMapper;
    private final OrderRepository orderRepository;
    private final UsedPointRepository usedPointRepository;
    private final SavedPointRepository savedPointRepository;
    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    private Point findPointByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return pointRepository.findByMember(member).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }

    public PointDto getPointByMember(String email) {
        Point point = findPointByEmail(email);
        return pointMapper.toDto(point);
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

    public Integer getTotalSavedPoint(String email) {
        Integer totalSavedPoint = savedPointRepository.findTotalSavedPoint(findPointByEmail(email));
        if (totalSavedPoint == null)
            return 0;
        return totalSavedPoint;
    }

    public Page<Point> getTotalPointList(String email, Pageable pageable) {
        return pointRepository.findAllByMemberEmailContaining(email, pageable);
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
}
