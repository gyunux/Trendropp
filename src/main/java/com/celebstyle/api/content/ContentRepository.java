package com.celebstyle.api.content;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    @Query("SELECT c FROM Content c JOIN FETCH c.celeb ORDER BY c.uploadDate DESC")
    List<Content> findAllWithCelebOrderByUploadDateDesc();
}
