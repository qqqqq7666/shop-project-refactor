package com.example.shop_project.point.controller;

import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/point")
public class PointViewController {
    @Autowired
    private PointService pointService;
    @Autowired
    private MemberService memberService;

    @GetMapping
    public String pointHistory(Principal principal, Model model, @RequestParam(defaultValue = "all") String category){
        model.addAttribute("pointHistoryList", pointService.getPointList(principal.getName(), category));
        model.addAttribute("point", pointService.getPointByMember(principal.getName()));
        model.addAttribute("category", category);
        model.addAttribute("member", memberService.findByEmail(principal.getName()));
        model.addAttribute("totalSavedPoint", pointService.getTotalSavedPoint(principal.getName()));
        return "/point/point_history";
    }
}
