package com.celebstyle.api.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.celebstyle.api.brand.Brand;
import com.celebstyle.api.brand.BrandRepository;
import com.celebstyle.api.common.S3UploadService;
import com.celebstyle.api.contentitem.ContentItemRepository;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.item.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ContentItemRepository contentItemRepository;

    @Mock
    private S3UploadService s3UploadService;

    private Brand createValidBrand() {
        return Brand.builder()
                .englishName("Nike")
                .koreanName("나이키")
                .build();
    }

    private Item createValidItem(String name, Brand brand) {
        return Item.builder()
                .name(name)
                .imageUrl("http://dummy.image")
                .productUrl("http://dummy.product")
                .brand(brand)
                .build();
    }

    @Test
    @DisplayName("아이템 생성 - [시나리오 1] 이미 존재하는 아이템이면 기존 아이템 반환")
    void createItem_Duplicate() throws IOException {
        ItemRequest request = new ItemRequest();
        request.setBrandId(1L);
        request.setItemName("Existing Item");

        Brand brand = createValidBrand();
        Item existingItem = createValidItem("Existing Item", brand);
        ReflectionTestUtils.setField(existingItem, "id", 1L);

        given(itemRepository.findByBrandIdAndName(1L, "Existing Item"))
                .willReturn(Optional.of(existingItem));

        Item result = itemService.createItem(request);

        assertThat(result).isEqualTo(existingItem);
        verify(s3UploadService, never()).upload(any(), any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("아이템 생성 - [시나리오 2] 새로운 아이템 정상 생성")
    void createItem_New_Success() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        ItemRequest request = new ItemRequest();
        request.setBrandId(1L);
        request.setItemName("New Item");
        request.setProductUrl("http://product.url");
        request.setItemImageFile(mockFile);

        Brand brand = createValidBrand();

        given(itemRepository.findByBrandIdAndName(1L, "New Item")).willReturn(Optional.empty());
        given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
        given(s3UploadService.upload(any(MultipartFile.class), eq("items"))).willReturn("http://s3.url/img.jpg");

        Item savedItem = createValidItem("New Item", brand);
        ReflectionTestUtils.setField(savedItem, "imageUrl", "http://s3.url/img.jpg");
        ReflectionTestUtils.setField(savedItem, "id", 10L);

        given(itemRepository.save(any(Item.class))).willReturn(savedItem);

        Item result = itemService.createItem(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getImageUrl()).isEqualTo("http://s3.url/img.jpg");
        verify(s3UploadService, times(1)).upload(any(), eq("items"));
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("아이템 생성 - [시나리오 3] 브랜드가 없으면 예외 발생")
    void createItem_BrandNotFound() {
        ItemRequest request = new ItemRequest();
        request.setBrandId(99L);
        request.setItemName("Item");

        given(itemRepository.findByBrandIdAndName(99L, "Item")).willReturn(Optional.empty());
        given(brandRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("아이템 생성 후 View 반환 테스트")
    void createItemAndGetView_Success() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        ItemRequest request = new ItemRequest();
        request.setBrandId(1L);
        request.setItemName("View Item");
        request.setItemImageFile(mockFile);
        request.setProductUrl("http://prod.url");

        Brand brand = createValidBrand();

        given(itemRepository.findByBrandIdAndName(any(), any())).willReturn(Optional.empty());
        given(brandRepository.findById(any())).willReturn(Optional.of(brand));
        given(s3UploadService.upload(any(), any())).willReturn("url");

        Item savedItem = createValidItem("View Item", brand);
        given(itemRepository.save(any())).willReturn(savedItem);

        ItemDetailView result = itemService.createItemAndGetView(request);

        assertThat(result.getName()).isEqualTo("View Item");
    }

    @Test
    @DisplayName("업데이트 혹은 생성 - [시나리오 1] 존재하면 URL 업데이트")
    void updateOrCreateItem_Update() throws IOException {
        ItemRequest request = new ItemRequest();
        request.setBrandId(1L);
        request.setItemName("Old Item");
        request.setProductUrl("http://new.url");
        request.setOriginalImageUrl("http://newImage.url");

        Brand brand = createValidBrand();
        Item existingItem = createValidItem("Old Item", brand);

        given(itemRepository.findByBrandIdAndName(1L, "Old Item")).willReturn(Optional.of(existingItem));

        Item result = itemService.updateOrCreateItem(request);

        assertThat(result.getProductUrl()).isEqualTo("http://new.url");
        assertThat(result.getImageUrl()).isEqualTo("http://newImage.url");
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("업데이트 혹은 생성 - [시나리오 2] 없으면 새로 생성")
    void updateOrCreateItem_Create() throws IOException {
        ItemRequest request = new ItemRequest();
        request.setBrandId(1L);
        request.setItemName("New Item");
        request.setItemImageFile(new MockMultipartFile("f", "d".getBytes()));
        request.setProductUrl("http://prod.url");

        Brand brand = createValidBrand();

        given(itemRepository.findByBrandIdAndName(1L, "New Item")).willReturn(Optional.empty());
        given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
        given(s3UploadService.upload(any(), any())).willReturn("url");

        Item newItem = createValidItem("New Item", brand);
        given(itemRepository.save(any())).willReturn(newItem);

        Item result = itemService.updateOrCreateItem(request);

        assertThat(result.getName()).isEqualTo("New Item");
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("전체 아이템 조회 테스트")
    void findAllItems_Success() {
        Brand brand = createValidBrand();
        Item item1 = createValidItem("Item1", brand);
        Item item2 = createValidItem("Item2", brand);

        given(itemRepository.findAll()).willReturn(List.of(item1, item2));

        List<ItemDetailView> result = itemService.findAllItems();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Item1");
    }

    @Test
    @DisplayName("아이템 수정 - [시나리오 1] 정상 수정")
    void updateItem_Success() {
        Long itemId = 1L;
        ItemRequest request = new ItemRequest();
        request.setBrandId(2L);
        request.setItemName("Updated Name");
        request.setProductUrl("Updated URL");

        Brand oldBrand = createValidBrand();
        Item existingItem = createValidItem("Old Name", oldBrand);

        Brand newBrand = createValidBrand();
        ReflectionTestUtils.setField(newBrand, "englishName", "New Brand");

        given(itemRepository.findById(itemId)).willReturn(Optional.of(existingItem));
        given(brandRepository.findById(2L)).willReturn(Optional.of(newBrand));

        itemService.updateItem(itemId, request);

        assertThat(existingItem.getName()).isEqualTo("Updated Name");
        assertThat(existingItem.getBrand()).isEqualTo(newBrand);
        assertThat(existingItem.getProductUrl()).isEqualTo("Updated URL");
    }

    @Test
    @DisplayName("아이템 수정 - [시나리오 2] 아이템 없음 예외")
    void updateItem_ItemNotFound() {
        given(itemRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(1L, new ItemRequest()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("아이템을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("아이템 수정 - [시나리오 3] 변경할 브랜드 없음 예외")
    void updateItem_BrandNotFound() {
        ItemRequest request = new ItemRequest();
        request.setBrandId(99L);

        Brand brand = createValidBrand();
        Item existingItem = createValidItem("Name", brand);

        given(itemRepository.findById(1L)).willReturn(Optional.of(existingItem));
        given(brandRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("브랜드를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("아이템 삭제 - [시나리오 1] 연관된 컨텐츠 삭제 후 아이템 삭제")
    void deleteItem_Success() {
        Brand brand = createValidBrand();
        Item item = createValidItem("Item", brand);

        given(itemRepository.findById(1L)).willReturn(Optional.of(item));

        itemService.deleteItem(1L);

        verify(contentItemRepository, times(1)).deleteByItem(item);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    @DisplayName("아이템 삭제 - [시나리오 2] 존재하지 않는 아이템 예외")
    void deleteItem_NotFound() {
        given(itemRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.deleteItem(1L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(contentItemRepository, never()).deleteByItem(any());
        verify(itemRepository, never()).delete(any());
    }
}