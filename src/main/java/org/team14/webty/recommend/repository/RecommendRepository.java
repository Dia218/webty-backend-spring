package org.team14.webty.recommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team14.webty.recommend.entity.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
}
