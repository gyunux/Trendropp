package com.celebstyle.api.item;

import com.celebstyle.api.item.dto.ItemDetailView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/items")
public class ItemAdminController {

    private final ItemService itemService;

    @GetMapping
    public String getItemList(Model model){
        List<ItemDetailView> itemDetailViewList = itemService.findAllItems();
        model.addAttribute("items",itemDetailViewList);
        model.addAttribute("currentPage", "items");
        return "admin/items";
    }
}
