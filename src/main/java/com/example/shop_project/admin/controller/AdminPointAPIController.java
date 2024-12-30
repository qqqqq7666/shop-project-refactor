package com.example.shop_project.admin.controller;

import com.example.shop_project.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin/points")
public class AdminPointAPIController {
    @Autowired
    private PointService pointService;

    @DeleteMapping("/savedpoint")
    public ResponseEntity<Void> cancelSavedPoint(@RequestBody Long savedPointId){
        pointService.cancelSavedPoint(savedPointId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usedpoint")
    public ResponseEntity<Void> cancelUsedPoint(@RequestBody Long usedPointId){
        pointService.cancelUsedPoint(usedPointId);
        return ResponseEntity.noContent().build();
    }
}
