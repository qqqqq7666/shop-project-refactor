package com.example.shop_project.point.controller;

import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/points")
public class PointAPIController {
    @Autowired
    private PointService pointService;

    @GetMapping
    public ResponseEntity<PointDto> getPoint(Principal principal){
        return ResponseEntity.ok(pointService.getPointByMember(principal.getName()));
    }

    @PostMapping("/save-point")
    public ResponseEntity<PointDto> savePoint(@RequestBody SavedPointRequestDto requestDto){
        log.warn("requestDto email = {}", requestDto.getEmail());
        PointDto response = pointService.createSavedPoint(requestDto);
        return ResponseEntity.created(URI.create("/mypage")).body(response);
    }

    @PostMapping("/use-point")
    public ResponseEntity<PointDto> usePoint(@RequestBody UsedPointRequestDto requestDto){
        PointDto response = pointService.createUsedPoint(requestDto);
        return ResponseEntity.created(URI.create("/mypage")).body(response);
    }
}
