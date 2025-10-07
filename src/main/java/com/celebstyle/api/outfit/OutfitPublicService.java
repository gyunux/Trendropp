package com.celebstyle.api.outfit;

import com.celebstyle.api.outfit.dto.OutfitDetailView;
import com.celebstyle.api.outfit.dto.OutfitPublicView;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutfitPublicService {
    private final OutfitRepository outfitRepository;

    @Transactional(readOnly = true)
    public List<OutfitPublicView> findAllForPublicView() {
        Sort sort = Sort.by(Sort.Direction.DESC, "uploadDate");

        return outfitRepository.findAll(sort).stream()
                .map(OutfitPublicView::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OutfitDetailView getOutfitDetail(Long id){
        Outfit outfit = outfitRepository.findById(id).orElseThrow();

        OutfitDetailView outfitDetailView = OutfitDetailView.fromEntity(outfit);
        return outfitDetailView;
    }
}
