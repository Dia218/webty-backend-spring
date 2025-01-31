package org.team14.webty.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team14.webty.user.entity.SocialProvider;

public interface SocialProviderRepository extends JpaRepository<SocialProvider, Long> {
	SocialProvider findByProviderId(String providerId);
}
