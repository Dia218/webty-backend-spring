package org.team14.webty.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	@Transactional
	@Modifying
	@Query("DELETE FROM RefreshToken u WHERE u.userId = :userId")
	void deleteByUserId(Long userId);

}
