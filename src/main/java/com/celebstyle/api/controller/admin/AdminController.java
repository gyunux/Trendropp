package com.celebstyle.api.controller.admin;

import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // private final CelebService celebService;
    // public AdminController(CelebService celebService) { this.celebService = celebService; }

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
        // List<CelebResponse> celebs = celebService.findAllCelebs();

        // 지금은 프론트엔드 개발을 위한 가짜(mock) 데이터를 사용합니다.
        List<CelebCreateResponse> celebs = List.of(

        );

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

}
