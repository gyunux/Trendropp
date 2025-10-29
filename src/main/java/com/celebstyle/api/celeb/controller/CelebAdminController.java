package com.celebstyle.api.celeb.controller;

import com.celebstyle.api.celeb.service.CelebService;
import com.celebstyle.api.celeb.dto.CelebAdminView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/celebs")
public class CelebAdminController {
    private final CelebService celebService;

    @GetMapping
    public String getCelebDashboard(Model model) {
        List<CelebAdminView> celebs = celebService.findAllForAdminView();
        model.addAttribute("celebs", celebs);
        model.addAttribute("currentPage", "celebs");

        return "admin/celebs";
    }
}
