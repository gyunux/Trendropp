package com.celebstyle.api.outfit;

import com.celebstyle.api.outfit.dto.OutfitPublicView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/outfits")
public class OutfitPublicApiController {

    private final OutfitPublicService outfitPublicService;

    @GetMapping
    public ResponseEntity<List<OutfitPublicView>> getAllOutfits() {
        List<OutfitPublicView> outfits = outfitPublicService.findAllForPublicView();
        return ResponseEntity.ok(outfits);
    }
}
