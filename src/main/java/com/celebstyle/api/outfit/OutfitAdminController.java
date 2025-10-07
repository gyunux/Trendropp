package com.celebstyle.api.outfit;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.ArticleService;
import com.celebstyle.api.brand.BrandService;
import com.celebstyle.api.brand.dto.BrandView;
import com.celebstyle.api.celeb.CelebService;
import com.celebstyle.api.celeb.dto.CelebView;
import com.celebstyle.api.outfit.dto.OutfitAdminView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/outfits")
@RequiredArgsConstructor
public class OutfitAdminController {
    private final OutfitAdminService outfitService;
    private final ArticleService articleService;
    private final CelebService celebService;
    private final BrandService brandService;

    @GetMapping
    public String getBrandDashboard(Model model){
        List<OutfitAdminView> outfits = outfitService.findAll();
        model.addAttribute("outfits",outfits);
        return "admin/outfits";
    }

    @GetMapping("/new")
    public String showOutfitCreateForm(@RequestParam Long articleId, Model model) {
        Article article = articleService.findById(articleId);
        model.addAttribute("article", article);

        addCommonAttributesToModel(model);
        return "admin/outfit-creator";
    }

    @GetMapping("/new/custom")
    public String showOutfitCreateCustomForm(Model model) {
        addCommonAttributesToModel(model);
        return "admin/outfit-custom-creator";
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
