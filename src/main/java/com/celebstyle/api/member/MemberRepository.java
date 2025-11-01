package com.celebstyle.api.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserIdAndDeletedAtIsNull(String userId);
    
    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);
}
