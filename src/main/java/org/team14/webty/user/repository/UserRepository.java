package org.team14.webty.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team14.webty.user.entity.WebtyUser;

public interface UserRepository extends JpaRepository<WebtyUser, Long> {
	Optional<WebtyUser> findByNickname(String nickname);
}
