package com.celebstyle.api.content;

import com.celebstyle.api.content.dto.ContentPublicView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/contents")
public class ContentPublicApiController {

    private final ContentPublicService contentPublicService;

    @GetMapping
    public ResponseEntity<List<ContentPublicView>> getAllContents() {
        List<ContentPublicView> contents = contentPublicService.findAllForPublicView();
        return ResponseEntity.ok(contents);
    }
}
