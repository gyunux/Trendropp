package com.celebstyle.api.article.controller;

import com.celebstyle.api.article.dto.ArticleAdminView;
import com.celebstyle.api.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/articles")
public class ArticleAdminController {
    private final ArticleService articleService;

    @GetMapping
    public String getArticleDashboard(Model model, Pageable pageable) {
        Page<ArticleAdminView> articlesPage = articleService.findAllForAdminView(pageable);
        int totalPages = articlesPage.getTotalPages();
        int currentPage = articlesPage.getNumber();

        int navSize = 10;
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
        model.addAttribute("articlesPage", articlesPage);
        model.addAttribute("currentPage", "articles");
        return "admin/articles";
    }
}
