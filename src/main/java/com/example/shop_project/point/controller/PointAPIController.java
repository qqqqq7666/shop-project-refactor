package com.example.shop_project.point.controller;

import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.service.PointService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/create/{memberId}")
    public ResponseEntity<Void> test(@PathVariable Long memberId){
        pointService.createPoint(memberId);
        return ResponseEntity.created(URI.create("/order")).build();
    }

    @GetMapping
    public ResponseEntity<PointDto> getPoint(Principal principal){
        return ResponseEntity.ok(pointService.getPointByMember(principal.getName()));
    }

    @PostMapping("/point-history")
    public ResponseEntity<PointDto> savePoint(@RequestBody PointHistoryDto pointHistoryDto) {
        PointDto response = pointService.createPointHistory(pointHistoryDto);

        return ResponseEntity.created(URI.create("/api/v1/points")).body(response);
    }
}
