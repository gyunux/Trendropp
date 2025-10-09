package com.celebstyle.api.brand;

import com.celebstyle.api.brand.dto.BrandView;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public String getBrandDashboard(Model model){
        List<BrandView> brands = brandService.findAll();
        model.addAttribute("brands",brands);
        model.addAttribute("currentPage", "brands");

        return "admin/brands";
    }
}
