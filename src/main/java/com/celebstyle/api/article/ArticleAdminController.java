package com.celebstyle.api.article;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/articles")
public class ArticleAdminController {
    private final ArticleService articleService;

    @GetMapping
    public String getArticleDashboard(Model model,@RequestParam(required = false) String mode){
        List<ArticleAdminView> articles = articleService.findAllForAdminView();
        model.addAttribute("articles",articles);

        // "mode" 파라미터가 "select"와 일치하는지 여부를 boolean으로 전달
        model.addAttribute("isSelectionMode", "select".equals(mode));

        return "admin/articles";
    }
}
