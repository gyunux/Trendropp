package com.celebstyle.api.outfit;

import com.celebstyle.api.outfit.dto.OutfitAdminView;
import com.celebstyle.api.outfit.dto.SaveOutfitRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/admin/outfits")
@RequiredArgsConstructor
public class OutfitAdminApiController {
    private final OutfitAdminService outfitService;

    @PostMapping
    public ResponseEntity<OutfitAdminView> createOutfit(@Valid @RequestBody SaveOutfitRequest request){
        OutfitAdminView createdOutfit = outfitService.createOutfit(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOutfit.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdOutfit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOutfit(@PathVariable Long id, @Valid @RequestBody SaveOutfitRequest request) {
        outfitService.updateOutfit(id, request);
        return ResponseEntity.ok().build(); // 성공 시 200 OK 응답
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOutfit(@PathVariable Long id){
        outfitService.deleteOutfit(id);
        return ResponseEntity.ok().build();
    }
}
