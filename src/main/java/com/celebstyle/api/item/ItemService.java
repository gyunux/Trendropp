package com.celebstyle.api.item;

import com.celebstyle.api.brand.Brand;
import com.celebstyle.api.brand.BrandRepository;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.outfititem.OutfitItemRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final BrandRepository brandRepository;
    private final OutfitItemRepository outfitItemRepository;
    //내부 사용
    @Transactional
    public Item createItem(ItemRequest request) {
        Optional<Item> duplicateItem = itemRepository.findByBrandIdAndName(
                request.getBrandId(),
                request.getItemName()
        );

        if (duplicateItem.isPresent()) {
            return duplicateItem.get();
        }
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("브랜드(ID: " + request.getBrandId() + ")를 찾을 수 없습니다."));

        log.info("Create Item Brand Name : {}",brand.getEnglishName());
        Item newItem = Item.builder()
                .brand(brand)
                .name(request.getItemName())
                .imageUrl(request.getItemImageUrl())
                .productUrl(request.getProductUrl())
                .build();

        return itemRepository.save(newItem);
    }

    @Transactional
    public ItemDetailView createItemAndGetView(ItemRequest request) {
        Item newItem = createItem(request);
        return ItemDetailView.fromEntity(newItem);
    }

    @Transactional
    public Item updateOrCreateItem(ItemRequest request){
        Optional<Item> existingItem = itemRepository.findByBrandIdAndName(
                request.getBrandId(),
                request.getItemName()
        );
        if(existingItem.isPresent()){
            Item item = existingItem.get();

            //save를 호출할 필요는 없음 더티 체킹에 의해 자동 업데이트
            item.setProductUrl(request.getProductUrl());
            item.setImageUrl(request.getItemImageUrl());

            return item;
        }
        return createItem(request);
    }

    @Transactional(readOnly = true)
    public List<ItemDetailView> findAllItems(){
        return itemRepository.findAll().stream()
                .map(ItemDetailView::fromEntity)
                .toList();
    }

    @Transactional
    public void updateItem(Long id,ItemRequest request){
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("아이템을 찾을 수 없습니다."));
        Brand brand = brandRepository.findById(request.getBrandId())
                        .orElseThrow(() -> new EntityNotFoundException("브랜드를 찾을 수 없습니다."));

        item.setBrand(brand);
        item.setName(request.getItemName());
        item.setImageUrl(request.getItemImageUrl());
        item.setProductUrl(request.getProductUrl());
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("아이템을 찾을 수 없습니다: " + id));

        outfitItemRepository.deleteByItem(item);

        itemRepository.delete(item);
    }
}
