package com.celebstyle.api.item;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.celebstyle.api.item.controller.ItemAdminController;
import com.celebstyle.api.item.dto.ItemDetailView;
import com.celebstyle.api.item.service.ItemService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ItemAdminController.class)
class ItemAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemList() throws Exception {
        ItemDetailView itemDetailView = mock(ItemDetailView.class);
        List<ItemDetailView> response = List.of(itemDetailView);

        given(itemService.findAllItems()).willReturn(response);

        mockMvc.perform(get("/admin/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", response))
                .andExpect(model().attribute("currentPage", "items"));
    }
}