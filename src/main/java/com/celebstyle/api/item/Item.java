package com.celebstyle.api.item;

import com.celebstyle.api.brand.Brand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2048)
    private String imageUrl;

    @Column(nullable = false, length = 2048)
    private String productUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder
    public Item(String name, String imageUrl, String productUrl, Brand brand) {
        if (name == null || imageUrl == null || productUrl == null) {
            throw new IllegalArgumentException("필수 값이 누락되었습니다.");
        }
        this.name = name;
        this.imageUrl = imageUrl;
        this.productUrl = productUrl;
        this.brand = brand;
    }
}
