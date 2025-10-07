package com.celebstyle.api.celeb;

import com.celebstyle.api.celeb.dto.CelebCreateRequest;
import com.celebstyle.api.celeb.dto.CelebCreateResponse;
import com.celebstyle.api.celeb.dto.CelebUpdateRequest;
import com.celebstyle.api.celeb.dto.CelebAdminView;
import com.celebstyle.api.celeb.dto.CelebView;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/admin/celebs")
@RequiredArgsConstructor
public class CelebAdminApiController {
    private final CelebService celebService;

    @PostMapping
    public ResponseEntity<CelebCreateResponse> createCeleb(@Valid @RequestBody CelebCreateRequest request){
        CelebCreateResponse resp = celebService.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resp.getId())
                .toUri();

        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<CelebView>> getAllCelebsForSelection() {
        List<CelebView> celebs = celebService.findAllForCelebsName();
        return ResponseEntity.ok(celebs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCeleb(@PathVariable Long id,
                                            @Valid @RequestBody CelebUpdateRequest request) {
        celebService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCeleb(@PathVariable Long id) {
        celebService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
