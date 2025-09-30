package com.celebstyle.api.celeb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CelebRepository extends JpaRepository<Celeb,Long> {
}
