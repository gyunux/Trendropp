package com.celebstyle.api.brand;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {
    boolean existsByEnglishName(String englishName);

    List<Brand> findAllByOrderByEnglishNameAsc();

    Brand findByEnglishName(String brandName);
}
