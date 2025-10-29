package com.celebstyle.api.content.controller.web;

import com.celebstyle.api.content.dto.ContentDetailView;
import com.celebstyle.api.content.service.ContentPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentPublicController {
    private final ContentPublicService contentPublicService;

    @GetMapping("/{id}")
    public String getContent(@PathVariable Long id, Model model){
        ContentDetailView contentDetailView = contentPublicService.getContentDetail(id);
        model.addAttribute("content", contentDetailView);
        return "content-detail";
    }
}
