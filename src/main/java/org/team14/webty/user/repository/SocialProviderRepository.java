package org.team14.webty.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.team14.webty.user.entity.SocialProvider;

@Repository
public interface SocialProviderRepository extends JpaRepository<SocialProvider, Long> {
	SocialProvider findByProviderId(String providerId);
}
