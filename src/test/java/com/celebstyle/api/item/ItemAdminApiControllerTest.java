package com.celebstyle.api.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.celebstyle.api.brand.Brand;
import com.celebstyle.api.item.controller.ItemAdminApiController;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.item.dto.ItemRequest;
import com.celebstyle.api.item.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ItemAdminApiController.class)
class ItemAdminApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[POST] 아이템 생성 API - Multipart 요청 (201 Created)")
    @WithMockUser(roles = "ADMIN")
    void createItemTest() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "itemImageFile",
                "test.jpg",
                "image/jpeg",
                "test data".getBytes()
        );

        Brand brand = Brand.builder()
                .englishName("Nike")
                .koreanName("나이키")
                .build();

        Item item = Item.builder()
                .name("New Item")
                .imageUrl("http://image.url")
                .productUrl("http://product.url")
                .brand(brand)
                .build();
        ReflectionTestUtils.setField(item, "id", 1L);

        ItemDetailView mockResponse = ItemDetailView.fromEntity(item);

        given(itemService.createItemAndGetView(any(ItemRequest.class))).willReturn(mockResponse);

        mockMvc.perform(multipart("/api/admin/items")
                        .file(mockFile)
                        .param("brandId", "1")
                        .param("itemName", "New Item")
                        .param("productUrl", "http://product.url")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Item"));

        verify(itemService).createItemAndGetView(any(ItemRequest.class));
    }

    @Test
    @DisplayName("[PUT] 아이템 수정 API - JSON 요청 (200 OK)")
    @WithMockUser(roles = "ADMIN")
    void updateItemTest() throws Exception {
        Long itemId = 1L;
        ItemRequest request = new ItemRequest();
        request.setBrandId(2L);
        request.setItemName("Updated Name");
        request.setProductUrl("http://updated.url");

        doNothing().when(itemService).updateItem(eq(itemId), any(ItemRequest.class));

        mockMvc.perform(put("/api/admin/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).updateItem(eq(itemId), any(ItemRequest.class));
    }

    @Test
    @DisplayName("[DELETE] 아이템 삭제 API - (200 OK)")
    @WithMockUser(roles = "ADMIN")
    void deleteItemTest() throws Exception {
        Long itemId = 1L;
        doNothing().when(itemService).deleteItem(itemId);

        mockMvc.perform(delete("/api/admin/items/{id}", itemId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).deleteItem(itemId);
    }
}