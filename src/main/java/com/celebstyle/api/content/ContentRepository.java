package com.celebstyle.api.content;

import com.celebstyle.api.celeb.dto.CelebViewStatDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    @Query("SELECT c FROM Content c JOIN FETCH c.celeb ORDER BY c.uploadDate DESC")
    List<Content> findAllWithCelebOrderByUploadDateDesc();

    @Query("SELECT new com.celebstyle.api.celeb.dto.CelebViewStatDto(" +
            "  c.celeb.id as celebId, " +
            "  c.celeb.nameKo as nameKo, " +
            "  c.celeb.nameEn as nameEn, " +
            "  SUM(c.viewCount) as totalViewCount) " +
            "FROM Content c " +
            "GROUP BY c.celeb.id, c.celeb.nameKo, c.celeb.nameEn " + // 셀럽별로 그룹화
            "ORDER BY totalViewCount DESC")
        // 조회수 높은 순으로 정렬
    List<CelebViewStatDto> getCelebViewStats();
}
