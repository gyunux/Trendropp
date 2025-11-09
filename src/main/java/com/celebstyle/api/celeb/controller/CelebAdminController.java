package com.celebstyle.api.celeb.controller;

import com.celebstyle.api.celeb.dto.CelebAdminView;
import com.celebstyle.api.celeb.dto.CelebViewStatDto;
import com.celebstyle.api.celeb.service.CelebService;
import com.celebstyle.api.celeb.service.StatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CelebAdminController {
    private final CelebService celebService;
    private final StatService statService;

    @GetMapping("/celebs")
    public String getCelebDashboard(Model model) {
        List<CelebAdminView> celebs = celebService.findAllForAdminView();
        model.addAttribute("celebs", celebs);
        model.addAttribute("currentPage", "celebs");

        return "admin/celebs";
    }

    @GetMapping("/stats")
    public String getStatsPage(Model model) {
        List<CelebViewStatDto> statsList = statService.getCelebViewStats();

        model.addAttribute("statsList", statsList);
        model.addAttribute("currentPage", "stats");
        return "admin/stats"; // templates/admin/stats.html
    }
}
