package com.celebstyle.api.outfititem;

import com.celebstyle.api.outfit.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutfitItemRepository extends JpaRepository<OutfitItem,Long> {
    void deleteAllByOutfit(Outfit outfit);

}
