package com.celebstyle.api.brand;

import com.celebstyle.api.brand.dto.BrandCreateRequest;
import com.celebstyle.api.brand.dto.BrandAdminView;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    @Transactional
    public BrandAdminView createBrand(BrandCreateRequest request) {
        if (brandRepository.existsByEnglishName(request.getEnglishName())) {
            throw new IllegalArgumentException("이미 존재하는 브랜드입니다: " + request.getEnglishName());
        }
        Brand brand = Brand.builder()
                .englishName(request.getEnglishName())
                .koreanName(request.getKoreanName())
                .build();

        Brand savedBrand = brandRepository.save(brand);
        return BrandAdminView.fromEntity(savedBrand);
    }

    @Transactional(readOnly = true)
    public List<BrandAdminView> findAll() {
        return brandRepository.findAllByOrderByEnglishNameAsc().stream()
                .map(BrandAdminView::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BrandAdminView findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("브랜드를 찾을 수 없습니다: " + id));
        return BrandAdminView.fromEntity(brand);
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new EntityNotFoundException("브랜드를 찾을 수 없습니다: " + id);
        }
        brandRepository.deleteById(id);
    }
}
