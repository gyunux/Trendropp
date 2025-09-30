package com.celebstyle.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
