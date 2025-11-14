package com.celebstyle.api.celeb.controller;

import com.celebstyle.api.celeb.dto.CelebAdminView;
import com.celebstyle.api.celeb.dto.CelebViewStatDto;
import com.celebstyle.api.celeb.service.CelebService;
import com.celebstyle.api.celeb.service.StatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public String getCelebDashboard(Model model, @PageableDefault Pageable pageable) {
        Page<CelebAdminView> celebsPage = celebService.findPaginatedForAdminView(pageable);
        int totalPages = celebsPage.getTotalPages();
        int currentPage = celebsPage.getNumber();

        int navSize = 5;
        int startPage = (currentPage / navSize) * navSize;
        int endPage = Math.min(startPage + navSize - 1, totalPages - 1);

        if (endPage < 0) {
            endPage = 0;
            startPage = 0;
        } else if (totalPages > 0 && endPage == totalPages - 1) {
            startPage = Math.max(0, endPage - navSize + 1);
        }
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("celebsPage", celebsPage);
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
