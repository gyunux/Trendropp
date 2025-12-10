package com.celebstyle.api.celeb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Celeb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "celeb_id")
    private Long id;

    @Column(nullable = false)
    private String nameKo;

    @Column(nullable = false)
    private String nameEn;

    @Column(length = 2048)
    private String profileImageUrl;

    @Column(length = 50)
    private String instagramName;

    @Builder
    public Celeb(String nameKo, String nameEn, String profileImageUrl, String instagramName) {
        if (nameKo == null || nameEn == null || profileImageUrl == null || instagramName == null) {
            throw new IllegalArgumentException("Cannot be Empty");
        }
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.profileImageUrl = profileImageUrl;
        this.instagramName = instagramName;
    }

    public void updateInfo(String nameKo, String nameEn, String instagramName) {
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.instagramName = instagramName;
    }

    public void updateInfo(String nameKo, String nameEn, String instagramName, String profileImageUrl) {
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.instagramName = instagramName;
        this.profileImageUrl = profileImageUrl;
    }
}
