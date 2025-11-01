package com.celebstyle.api.article.controller;

import com.celebstyle.api.article.dto.ArticleAdminView;
import com.celebstyle.api.article.service.ArticleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleAdminApiController {
    private final ArticleService articleService;

    @GetMapping
    public String getArticleDashboard(Model model){
        List<ArticleAdminView> articles = articleService.findAllForAdminView();
        model.addAttribute("articles",articles);
        return "admin/articles";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCeleb(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
