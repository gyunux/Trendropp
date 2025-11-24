package com.celebstyle.api.like;

import com.celebstyle.api.member.Member;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {
    Optional<ContentLike> findByMemberIdAndContentId(Long memberId, Long contentId);

    @Query("SELECT cl FROM ContentLike cl " +
            "JOIN FETCH cl.content c " +
            "JOIN FETCH c.celeb " +
            "WHERE cl.member.id = :memberId " +
            "ORDER BY cl.id DESC")
    List<ContentLike> findByMemberIdWithContentAndCeleb(@Param("memberId") Long memberId);

    @Query("SELECT cl.content.id FROM ContentLike cl WHERE cl.member.id = :memberId")
    Set<Long> findLikedContentIdsByMemberId(@Param("memberId") Long memberId);

    boolean existsByMemberIdAndContentId(Long memberId, Long contentId);

    ContentLike findByMember(Member member);
}
