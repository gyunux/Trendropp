package com.celebstyle.api.article.controller;

import com.celebstyle.api.article.dto.ArticleAdminView;
import com.celebstyle.api.article.service.ArticleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public String getArticleDashboard(Model model) {
        List<ArticleAdminView> articles = articleService.findAllForAdminView();
        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", "articles");
        return "admin/articles";
    }
}
