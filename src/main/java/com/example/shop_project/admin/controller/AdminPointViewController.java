package com.example.shop_project.admin.controller;

import com.example.shop_project.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/point")
public class AdminPointViewController {
    @Autowired
    private PointService pointService;

    @GetMapping
    public String pointList(Model model, @RequestParam(defaultValue = "") String email, @RequestParam(defaultValue = "0") Integer page){
        model.addAttribute("pointPage", pointService.getTotalPointList(email, PageRequest.of(page, 10)));
        model.addAttribute("keyword", email);
        model.addAttribute("currentPage", page);

        return "admin/admin_point_list";
    }

    @GetMapping("/{pointId}")
    public String pointHistory(Model model, @PathVariable Long pointId, @RequestParam(defaultValue = "all") String category){
        model.addAttribute("pointHistoryList", pointService.getTotalPointHistory(pointId, category));
        model.addAttribute("point", pointService.getPointById(pointId));
        model.addAttribute("member", pointService.getMemberByPointId(pointId));
        model.addAttribute("category", category);

        return "admin/admin_point_history";
    }
}
