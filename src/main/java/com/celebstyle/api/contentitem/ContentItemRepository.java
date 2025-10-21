package com.celebstyle.api.contentitem;

import com.celebstyle.api.content.Content;
import com.celebstyle.api.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentItemRepository extends JpaRepository<ContentItem,Long> {
    void deleteAllByContent(Content content);
    void deleteByItem(Item item);
}
