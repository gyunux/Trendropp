package com.celebstyle.api.outfit;

import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.celeb.CelebRepository;
import com.celebstyle.api.item.Item;
import com.celebstyle.api.item.ItemService;
import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.outfit.dto.OutfitAdminView;
import com.celebstyle.api.outfit.dto.OutfitDetailView;
import com.celebstyle.api.outfit.dto.SaveOutfitRequest;
import com.celebstyle.api.outfititem.OutfitItem;
import com.celebstyle.api.outfititem.OutfitItemRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutfitAdminService {
    private final OutfitRepository outfitRepository;
    private final CelebRepository celebRepository;
    private final ItemService itemService;
    private final OutfitItemRepository outfitItemRepository;

    @Transactional
    public OutfitAdminView createOutfit(SaveOutfitRequest request){
        Celeb celeb = celebRepository.findById(request.getCelebId()).orElseThrow();

        Outfit newOutfit = Outfit.builder()
                .title(request.getTitle())
                .originImageUrl(request.getMainImageUrl())
                .sourceUrl(request.getSourceUrl())
                .sourceDate(request.getSourceDate())
                .sourceType(request.getSourceType())
                .celeb(celeb)
                .build();
        outfitRepository.save(newOutfit);

        for (ItemRequest itemDto : request.getItems()) {

            Item newItem = itemService.createItem(itemDto);

            OutfitItem outfitItem = new OutfitItem(newOutfit, newItem);
            outfitItemRepository.save(outfitItem);
        }

        return OutfitAdminView.fromEntity(newOutfit);
    }

    @Transactional(readOnly = true)
    public List<OutfitAdminView> findAll() {
        return outfitRepository.findAll().stream()
                .map(OutfitAdminView::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OutfitDetailView getOutfit(Long id){
        Outfit outfit = outfitRepository.findById(id).orElseThrow();
        return OutfitDetailView.fromEntity(outfit);
    }

    @Transactional
    public void updateOutfit(Long outfitId, SaveOutfitRequest request) {
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("착장을 찾을 수 없습니다."));

        Celeb celeb = celebRepository.findById(request.getCelebId()).orElseThrow();
        outfit.setCeleb(celeb);
        outfit.setOriginImageUrl(request.getMainImageUrl());
        outfit.setSourceType(request.getSourceType());
        outfit.setSourceUrl(request.getSourceUrl());
        outfit.setSourceDate(request.getSourceDate());

        outfitItemRepository.deleteAllByOutfit(outfit);

        for (ItemRequest itemDto : request.getItems()) {
            Item item = itemService.updateOrCreateItem(itemDto);

            OutfitItem outfitItem = new OutfitItem(outfit, item);
            outfitItemRepository.save(outfitItem);
        }
    }

    @Transactional
    public void deleteOutfit(Long id) {
        if (!outfitRepository.existsById(id)) {
            throw new EntityNotFoundException("착장정보를 찾을 수 없습니다: " + id);
        }
        Outfit outfit = outfitRepository.findById(id).orElseThrow();
        outfit.setDeleted(true);
    }

}
