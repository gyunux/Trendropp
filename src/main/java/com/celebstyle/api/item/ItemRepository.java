package com.celebstyle.api.item;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByBrandIdAndName(Long brandId, String itemName);

    @Query("SELECT ci.item FROM ContentItem ci " +
            "JOIN ci.item " +
            "WHERE ci.content.id = :contentId")
    List<Item> findItemsByContentId(@Param("contentId") Long contentId);

}
