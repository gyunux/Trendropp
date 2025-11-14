package com.celebstyle.api.brand.service;

import com.celebstyle.api.brand.Brand;
import com.celebstyle.api.brand.BrandRepository;
import com.celebstyle.api.brand.dto.BrandCreateRequest;
import com.celebstyle.api.brand.dto.BrandView;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    @Transactional
    public BrandView createBrand(BrandCreateRequest request) {
        if (brandRepository.existsByEnglishName(request.getEnglishName())) {
            throw new IllegalArgumentException("이미 존재하는 브랜드입니다: " + request.getEnglishName());
        }
        Brand brand = Brand.builder()
                .englishName(request.getEnglishName())
                .koreanName(request.getKoreanName())
                .build();

        Brand savedBrand = brandRepository.save(brand);
        return BrandView.fromEntity(savedBrand);
    }

    @Transactional(readOnly = true)
    public Page<BrandView> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable)
                .map(brand -> new BrandView(
                        brand.getId(),
                        brand.getEnglishName(),
                        brand.getKoreanName()
                ));
    }

    @Transactional(readOnly = true)
    public List<BrandView> findAllBrandsName() {
        return brandRepository.findAllByOrderByEnglishNameAsc().stream()
                .map(BrandView::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BrandView findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("브랜드를 찾을 수 없습니다: " + id));
        return BrandView.fromEntity(brand);
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new EntityNotFoundException("브랜드를 찾을 수 없습니다: " + id);
        }
        brandRepository.deleteById(id);
    }
}
