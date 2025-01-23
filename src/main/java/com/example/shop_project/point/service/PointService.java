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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {
    private final PointManagementService pointManagementService;
    private final PointRetrieveService pointRetrieveService;

    // 회원가입할 때 같이 생성
    public void createPointByEmail(String email) {
        pointManagementService.createPointByEmail(email);
    }

    public PointDto createSavedPoint(SavedPointRequestDto requestDto) {
        return pointManagementService.createSavedPoint(requestDto);
    }

    public PointDto createUsedPoint(UsedPointRequestDto requestDto) {
        return pointManagementService.createUsedPoint(requestDto);
    }

    public void cancelSavedPoint(Long savedPointId) {
        pointManagementService.cancelSavedPoint(savedPointId);
    }

    public void cancelUsedPoint(Long usedPointId) {
        pointManagementService.cancelUsedPoint(usedPointId);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void monthlyPointSave() {
        pointManagementService.monthlyPointSave();
    }

    public PointDto getPointByMember(String email) {
        return pointRetrieveService.getPointByMember(email);
    }

    public UsedPointResponseDto getUsedPointByOrderNo(Long orderNo) {
        return pointRetrieveService.getUsedPointByOrderNo(orderNo);
    }

    public List<PointHistoryDto> getPointList(String email, String category) {
        return pointRetrieveService.getPointList(email, category);
    }

    public Integer getTotalSavedPoint(String email) {
        return pointRetrieveService.getTotalSavedPoint(email);
    }

    public Page<Point> getTotalPointList(String email, Pageable pageable) {
        return pointRetrieveService.getTotalPointList(email, pageable);
    }

    // admin
    public List<PointHistoryDto> getTotalPointHistory(Long pointId, String category) {
        return pointRetrieveService.getTotalPointHistory(pointId, category);
    }

    public Point getPointById(Long pointId) {
        return pointRetrieveService.getPointById(pointId);
    }

    public Member getMemberByPointId(Long pointId) {
        return pointRetrieveService.getMemberByPointId(pointId);
    }
}
