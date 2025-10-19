package com.celebstyle.api.outfit;

import com.celebstyle.api.celeb.Celeb;
import com.celebstyle.api.outfititem.OutfitItem;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Outfit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="outfit_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false,length = 2048)
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
    @JoinColumn(name="celeb_id")
    private Celeb celeb;

    @OneToMany(mappedBy = "outfit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutfitItem> outfitItems = new ArrayList<>();

    private boolean deleted;

    @Lob
    private String summary;

    @Builder
    public Outfit(String title,
                  String originImageUrl,
                  String summary,
                  String sourceUrl,
                  LocalDateTime sourceDate,
                  SourceType sourceType,
                  Celeb celeb){
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
