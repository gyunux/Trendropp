package com.celebstyle.api.content.dto;

import com.celebstyle.api.celeb.dto.CelebPublicView;
import com.celebstyle.api.content.Content;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContentPublicView {
    private Long id;
    private String title;
    private String originImageUrl;
    private CelebPublicView celeb;
    private int itemCount;
    private boolean isLiked;

    public static ContentPublicView fromEntity(Content content, boolean isLiked, Locale locale) {
        boolean isEnglish = locale.getLanguage().equals("en");

        return new ContentPublicView(
                content.getId(),
                isEnglish ? content.getTitleEn() : content.getTitleKo(),
                content.getOriginImageUrl(),
                new CelebPublicView(content.getCeleb(), locale),
                content.getItemCount(),
                isLiked
        );
    }
}