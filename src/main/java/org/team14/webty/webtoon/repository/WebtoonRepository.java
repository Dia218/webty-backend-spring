package org.team14.webty.webtoon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.team14.webty.webtoon.entity.Webtoon;

@Repository
public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {
}
