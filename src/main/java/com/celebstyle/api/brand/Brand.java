package com.celebstyle.api.brand;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    private String englishName;
    private String koreanName;

    @Builder
    public Brand(String englishName,String koreanName) {
        if(englishName == null || koreanName == null){
            throw new IllegalArgumentException("Cannot be Empty");
        }
        this.englishName = englishName;
        this.koreanName = koreanName;
    }
}
