package com.celebstyle.api.outfit;

import com.celebstyle.api.outfit.dto.OutfitDetailView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/outfits")
public class OutfitPublicController {
    private final OutfitPublicService outfitPublicService;

    @GetMapping("/{id}")
    public String getOutfit(@PathVariable Long id, Model model){
        OutfitDetailView outfitDetailView = outfitPublicService.getOutfitDetail(id);
        model.addAttribute("outfit",outfitDetailView);
        return "outfit-detail";
    }
}
