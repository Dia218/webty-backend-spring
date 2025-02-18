package org.team14.webty.voting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.team14.webty.user.entity.SocialProvider;
import org.team14.webty.user.entity.WebtyUser;
import org.team14.webty.user.enumerate.SocialProviderType;
import org.team14.webty.user.repository.UserRepository;
import org.team14.webty.voting.entity.Similar;
import org.team14.webty.voting.entity.Vote;
import org.team14.webty.voting.enumerate.VoteType;
import org.team14.webty.voting.repository.SimilarRepository;
import org.team14.webty.voting.repository.VoteRepository;
import org.team14.webty.webtoon.entity.Webtoon;
import org.team14.webty.webtoon.enumerate.Platform;
import org.team14.webty.webtoon.repository.WebtoonRepository;

@TestComponent
public class VotingTestDataInitializer {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WebtoonRepository webtoonRepository;
	@Autowired
	private SimilarRepository similarRepository;
	@Autowired
	private VoteRepository voteRepository;

	public void deleteAllData() {
		voteRepository.deleteAll();
		similarRepository.deleteAll();
		webtoonRepository.deleteAll();
		userRepository.deleteAll();
	}

	public WebtyUser initTestUser() {
		return userRepository.save(WebtyUser.builder()
			.nickname("유사웹툰등록자")
			.profileImage("similarTestImg")
			.socialProvider(SocialProvider.builder()
				.provider(SocialProviderType.KAKAO)
				.providerId("123456789")
				.build())
			.build());
	}

	public Webtoon newTestTargetWebtoon(int number) {
		return webtoonRepository.save(Webtoon.builder()
			.webtoonName("테스트 투표 대상 웹툰" + number)
			.platform(Platform.KAKAO_PAGE)
			.webtoonLink("www.testTargetWebtoon" + number)
			.thumbnailUrl("testTargetWebtoon.jpg" + number)
			.authors("testTargetWebtoonAuthor" + number)
			.finished(true)
			.build());
	}

	public Webtoon newTestChoiceWebtoon(int number) {
		return webtoonRepository.save(Webtoon.builder()
			.webtoonName("테스트 선택 대상 웹툰" + number)
			.platform(Platform.KAKAO_PAGE)
			.webtoonLink("www.testChoiceWebtoon" + number)
			.thumbnailUrl("testChoiceWebtoon.jpg" + number)
			.authors("testChoiceWebtoonAuthor" + number)
			.finished(true)
			.build());
	}

	public Similar newTestSimilar(WebtyUser testUser, Webtoon testTargetWebtoon, Webtoon testChoiceWebtoon) {
		return similarRepository.save(Similar.builder()
			.similarWebtoonId(testChoiceWebtoon.getWebtoonId())
			.similarResult(0L)
			.userId(testUser.getUserId())
			.targetWebtoon(testTargetWebtoon)
			.build());
	}

	public Vote newTestVote(WebtyUser testUser, Similar testSimilar, VoteType voteType) {
		return voteRepository.save(Vote.builder()
			.userId(testUser.getUserId())
			.similar(testSimilar)
			.voteType(voteType)
			.build());
	}
}
