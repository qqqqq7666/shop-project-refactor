package com.example.shop_project.point.controller;

import com.example.shop_project.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@Controller
public class PointViewController {
    @Autowired
    private PointService pointService;

    @GetMapping("/point-history")
    public String pointHistory(Principal principal, Model model){
        model.addAttribute("point", pointService.getPointByMember(principal.getName()));
        return "/point/point_history";
    }
}
