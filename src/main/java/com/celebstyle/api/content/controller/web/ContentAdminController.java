package com.celebstyle.api.content.controller.web;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.service.ArticleService;
import com.celebstyle.api.brand.dto.BrandView;
import com.celebstyle.api.brand.service.BrandService;
import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.celeb.service.CelebService;
import com.celebstyle.api.content.SourceType;
import com.celebstyle.api.content.dto.ContentAdminView;
import com.celebstyle.api.content.service.ContentAdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/contents")
@RequiredArgsConstructor
public class ContentAdminController {
    private final ContentAdminService contentService;
    private final ArticleService articleService;
    private final CelebService celebService;
    private final BrandService brandService;

    @GetMapping
    public String getBrandDashboard(Model model, Pageable pageable) {
        Page<ContentAdminView> contentsPage = contentService.findAll(pageable);
        int totalPages = contentsPage.getTotalPages();
        int currentPage = contentsPage.getNumber();

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
        model.addAttribute("contentsPage", contentsPage);
        model.addAttribute("currentPage", "contents");

        return "admin/contents";
    }

    @GetMapping("/new")
    public String showContentCreateForm(@RequestParam Long articleId, Model model) {
        Article article = articleService.findById(articleId);
        model.addAttribute("article", article);

        addCommonAttributesToModel(model);
        return "admin/content-creator";
    }

    @GetMapping("/new/custom")
    public String showContentCreateCustomForm(Model model) {
        addCommonAttributesToModel(model);
        return "admin/content-custom-creator";
    }

    private void addCommonAttributesToModel(Model model) {
        List<CelebView> celebs = celebService.findAllForCelebsName();
        List<BrandView> brands = brandService.findAllBrandsName();
        SourceType[] sourceTypes = SourceType.values();

        model.addAttribute("celebs", celebs);
        model.addAttribute("brands", brands);
        model.addAttribute("sourceTypes", sourceTypes);
    }
}
