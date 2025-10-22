package com.celebstyle.api.content;

import com.celebstyle.api.content.dto.ContentAdminView;
import com.celebstyle.api.content.dto.ContentDetailView;
import com.celebstyle.api.content.dto.SaveContentRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/admin/contents")
@RequiredArgsConstructor
public class ContentAdminApiController {
    private final ContentAdminService contentService;

    @PostMapping
    public ResponseEntity<ContentAdminView> createContent(@Valid @ModelAttribute SaveContentRequest request)
            throws IOException {
        ContentAdminView createdContent = contentService.createContent(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdContent.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdContent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentDetailView> getContent(@PathVariable Long id){
        ContentDetailView contentDetailView = contentService.getContent(id);
        return ResponseEntity.ok(contentDetailView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateContent(@PathVariable Long id, @Valid @RequestBody SaveContentRequest request) {
        contentService.updateContent(id, request);
        return ResponseEntity.ok().build(); // 성공 시 200 OK 응답
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id){
        contentService.deleteContent(id);
        return ResponseEntity.ok().build();
    }
}
