package com.celebstyle.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Outfit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="outfit_id")
    private Long id;

    @Column(nullable = false,length = 2048)
    private String originImageUrl;

    private String source;
    private LocalDateTime sourceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="celeb_id")
    private Celeb celeb;
}
