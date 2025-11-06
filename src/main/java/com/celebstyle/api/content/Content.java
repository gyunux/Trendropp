package com.celebstyle.api.content;

import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.contentitem.ContentItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2048)
    private String originImageUrl;

    private String sourceUrl;

    private LocalDateTime sourceDate;

    @CreationTimestamp
    private LocalDateTime uploadDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "celeb_id")
    private Celeb celeb;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentItem> contentItems = new ArrayList<>();

    private boolean deleted;

    @Formula("(select count(1) from content_item ci where ci.content_id = content_id)")
    private int itemCount;
    
    @Lob
    private String summary;

    @Builder
    public Content(String title,
                   String originImageUrl,
                   String summary,
                   String sourceUrl,
                   LocalDateTime sourceDate,
                   SourceType sourceType,
                   Celeb celeb) {
        this.title = title;
        this.originImageUrl = originImageUrl;
        this.summary = summary;
        this.sourceUrl = sourceUrl;
        this.sourceDate = sourceDate;
        this.sourceType = sourceType;
        this.celeb = celeb;
        this.deleted = false;
    }
}
