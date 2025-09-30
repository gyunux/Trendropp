package com.celebstyle.api.celeb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Celeb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="celeb_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2048)
    private String profileImageUrl;

    @Column(length = 50)
    private String instagramName;

    @Builder
    public Celeb(String name,String profileImageUrl,String instagramName){
        if(name == null || profileImageUrl == null || instagramName == null){
            throw new IllegalArgumentException("Cannot be Empty");
        }
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.instagramName = instagramName;
    }
}
