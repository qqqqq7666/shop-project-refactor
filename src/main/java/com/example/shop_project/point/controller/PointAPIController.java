package com.example.shop_project.point.controller;

import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/points")
public class PointAPIController {
    @Autowired
    PointService pointService;

    @PostMapping("/create/{memberId}")
    public ResponseEntity<Void> test(@PathVariable Long memberId){
        pointService.createPoint(memberId);
        return ResponseEntity.created(URI.create("/order")).build();
    }

    @GetMapping
    public ResponseEntity<PointDto> getPoint(Principal principal){
        return ResponseEntity.ok(pointService.getPointByMember(principal.getName()));
    }

    @GetMapping("/saved-point")
    public ResponseEntity<SavedPointResponseDto> getSavedPointList(Principal principal){
        return null;
    }

    @PostMapping("/save-point")
    public ResponseEntity<PointDto> savePoint(@RequestBody SavedPointRequestDto requestDto){
        PointDto response = pointService.createSavedPoint(requestDto);
        return ResponseEntity.created(URI.create("/mypage")).body(response);
    }

    @PostMapping("use-point")
    public ResponseEntity<PointDto> usePoint(@RequestBody UsedPointRequestDto responseDto){
        PointDto response = pointService.createUsedPoint(responseDto);
        return ResponseEntity.created(URI.create("/mypage")).body(response);
    }

//    @PostMapping("/use-point")
//    public ResponseEntity<PointDto> usePoint(@RequestBody)
}
