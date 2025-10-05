package com.celebstyle.api.controller.admin;

import com.celebstyle.api.article.ArticleAdminView;
import com.celebstyle.api.article.ArticleService;
import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebService;
import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import com.celebstyle.api.celeb.dto.CelebView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CelebService celebService;
    private final ArticleService articleService;
    @GetMapping
    public String adminRoot(Model model) {
        model.addAttribute("totalCelebs",null);
        model.addAttribute("totalOutfits",null);
        return "redirect:/admin/dashboard";
    }

    /**
     * 셀럽 관리 대시보드 페이지를 렌더링하고, 셀럽 목록 데이터를 전달합니다.
     */
    @GetMapping("/celebs")
    public String getCelebDashboard(Model model) {
        // 실제로는 Service를 통해 DB에서 데이터를 가져옵니다.
         List<CelebView> celebs = celebService.findAllForAdminView();
        model.addAttribute("celebs", celebs);

        // Thymeleaf가 /resources/templates/admin/celebs.html 파일을 찾도록 합니다.
        // 파일 이름을 dashboard.html에서 celebs.html로 변경하는 것이 더 명확합니다.
        return "admin/celebs";
    }

    /**
     * 착장 관리 페이지 (아직 비어있음)
     */
//    @GetMapping("/outfits")
//    public String getOutfitDashboard() {
//        // /resources/templates/admin/outfits.html 파일을 찾습니다.
//        return "admin/outfits";
//    }

    @GetMapping("/articles")
    public String getArticleDashboard(Model model){
        List<ArticleAdminView> articles = articleService.findAllForAdminView();
        model.addAttribute("articles",articles);

        return "admin/articles";
    }
}
