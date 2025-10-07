package com.celebstyle.api.item;

import com.celebstyle.api.brand.Brand;
import com.celebstyle.api.brand.BrandRepository;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.item.dto.ItemRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final BrandRepository brandRepository;

    //내부 사용
    @Transactional
    public Item createItem(ItemRequest request) {
        Brand brand = brandRepository.findByEnglishName(request.getBrandName());

        Item newItem = Item.builder()
                .brand(brand)
                .name(request.getItemName())
                .imageUrl(request.getItemImageUrl())
                .productUrl(request.getProductUrl())
                .build();

        return itemRepository.save(newItem);
    }

    //API
    @Transactional
    public ItemDetailView createItemAndGetView(ItemRequest request) {
        Item newItem = createItem(request);
        return ItemDetailView.fromEntity(newItem);
    }

    @Transactional(readOnly = true)
    public ItemDetailView findById(Long id){
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("아이템을 찾을 수 없습니다."));
        return ItemDetailView.fromEntity(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new EntityNotFoundException("아이템을 찾을 수 없습니다: " + id);
        }
        itemRepository.deleteById(id);
    }
}
