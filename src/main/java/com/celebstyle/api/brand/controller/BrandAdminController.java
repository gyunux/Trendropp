package com.celebstyle.api.brand.controller;

import com.celebstyle.api.brand.dto.BrandView;
import com.celebstyle.api.brand.service.BrandService;
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
@RequestMapping("/admin/brands")
public class BrandAdminController {
    private final BrandService brandService;

    @GetMapping
    public String getBrandDashboard(Model model, @PageableDefault(size = 20) Pageable pageable) {
        Page<BrandView> brandsPage = brandService.findAll(pageable);

        int totalPages = brandsPage.getTotalPages();
        int currentPage = brandsPage.getNumber();

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
        model.addAttribute("brandsPage", brandsPage);
        model.addAttribute("currentPage", "brands");

        return "admin/brands";
    }
}
