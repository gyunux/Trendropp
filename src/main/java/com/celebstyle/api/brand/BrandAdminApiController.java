package com.celebstyle.api.brand;

import com.celebstyle.api.brand.dto.BrandCreateRequest;
import com.celebstyle.api.brand.dto.BrandAdminView;
import com.celebstyle.api.brand.dto.BrandView;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
public class BrandAdminApiController {
    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<BrandAdminView> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        BrandAdminView createdBrand = brandService.createBrand(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBrand.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdBrand);
    }

    @GetMapping
    public ResponseEntity<List<BrandView>> getAllBrandsForSelection() {
        List<BrandView> brands = brandService.findAllBrandsName();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandAdminView> getBrandById(@PathVariable Long id) {
        BrandAdminView brand = brandService.findById(id);
        return ResponseEntity.ok(brand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
