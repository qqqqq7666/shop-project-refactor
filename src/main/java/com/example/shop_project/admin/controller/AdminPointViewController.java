package com.example.shop_project.admin.controller;

import com.example.shop_project.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/point")
public class AdminPointViewController {
    @Autowired
    private PointService pointService;

    @GetMapping
    public String pointList(Model model){
        model.addAttribute("pointList", pointService.getTotalPointList());

        return "admin/admin_point_list";
    }
}
