# webty-backend-spring
> 프로그래머스 데브코스: 팀 프로젝트 #2 백엔드

한국 웹툰 커뮤니티 'WEBTY'  
백엔드


<br>

# WEBTY

10대를 위한 웹툰 커뮤니티 플랫폼  
사용자들은 웹툰 리뷰를 공유하고, 유사한 웹툰에 대해 토론할 수 있습니다  


<br>

## 기술 스택

- **언어 및 프레임워크:** Java 17, Spring Boot 3
- **인증:** Spring Security, JWT, OAuth2 (Kakao)
- **데이터베이스:** MySQL, H2 (테스트)
- **캐시 및 세션 관리:** Redis
- **API 문서화:** Swagger


<br>

## Swagger

swagger: [서버 실행 후 접속](http://localhost:8080/swagger-ui/index.html)  
인증이 필요한 요청은 Authorize 버튼을 눌러 'Token'을 입력한다  
'Token'은 카카오로그인 후 개발자도구에서 확인한다  


<br>

## 디렉터리 구조

```
├─main
│  ├─generated
│  ├─java
│  │  └─org
│  │      └─team14
│  │          └─webty
│  │              │  WebtyApplication.java
│  │              │
│  │              ├─common
│  │              │  ├─config
│  │              │  │      RestTemplateConfig.java
│  │              │  │      SchedulerConfig.java
│  │              │  │      SwaggerConfig.java
│  │              │  │      WebConfig.java
│  │              │  │
│  │              │  ├─cookies
│  │              │  │      CookieManager.java
│  │              │  │
│  │              │  ├─dto
│  │              │  │      PageDto.java
│  │              │  │
│  │              │  ├─enums
│  │              │  │      TokenType.java
│  │              │  │
│  │              │  ├─exception
│  │              │  │      BusinessException.java
│  │              │  │      ErrorCode.java
│  │              │  │      ErrorDetails.java
│  │              │  │      GlobalExceptionHandler.java
│  │              │  │
│  │              │  ├─mapper
│  │              │  │      PageMapper.java
│  │              │  │
│  │              │  └─util
│  │              │          FileStorageUtil.java
│  │              │
│  │              ├─recommend
│  │              │  ├─controller
│  │              │  │      RecommendController.java
│  │              │  │
│  │              │  ├─entity
│  │              │  │      Recommend.java
│  │              │  │
│  │              │  ├─enumerate
│  │              │  │      LikeType.java
│  │              │  │
│  │              │  ├─repository
│  │              │  │      RecommendRepository.java
│  │              │  │
│  │              │  └─service
│  │              │          RecommendService.java
│  │              │
│  │              ├─review
│  │              │  ├─controller
│  │              │  │      ReviewController.java
│  │              │  │
│  │              │  ├─dto
│  │              │  │      ReviewDetailResponse.java
│  │              │  │      ReviewItemResponse.java
│  │              │  │      ReviewRequest.java
│  │              │  │      SearchReviewPageResponse.java
│  │              │  │
│  │              │  ├─entity
│  │              │  │      Review.java
│  │              │  │      ReviewImage.java
│  │              │  │
│  │              │  ├─enumrate
│  │              │  │      SpoilerStatus.java
│  │              │  │
│  │              │  ├─mapper
│  │              │  │      ReviewMapper.java
│  │              │  │
│  │              │  ├─repository
│  │              │  │      ReviewImageRepository.java
│  │              │  │      ReviewRepository.java
│  │              │  │
│  │              │  └─service
│  │              │          RecommendService.java
│  │              │          ReviewService.java
│  │              │
│  │              ├─reviewComment
│  │              │  ├─controller
│  │              │  │      ReviewCommentController.java
│  │              │  │
│  │              │  ├─dto
│  │              │  │      CommentRequest.java
│  │              │  │      CommentResponse.java
│  │              │  │
│  │              │  ├─entity
│  │              │  │      ReviewComment.java
│  │              │  │
│  │              │  ├─mapper
│  │              │  │      ReviewCommentMapper.java
│  │              │  │
│  │              │  ├─repository
│  │              │  │      ReviewCommentRepository.java
│  │              │  │
│  │              │  └─service
│  │              │          ReviewCommentService.java
│  │              │
│  │              ├─security
│  │              │  ├─authentication
│  │              │  │      AuthWebtyUserProvider.java
│  │              │  │      CustomAuthenticationFilter.java
│  │              │  │      WebtyUserDetails.java
│  │              │  │      WebtyUserDetailsService.java
│  │              │  │
│  │              │  ├─config
│  │              │  │      RedisConfig.java
│  │              │  │      SecurityConfig.java
│  │              │  │
│  │              │  ├─oauth2
│  │              │  │      LoginSuccessHandler.java
│  │              │  │      LogoutSuccessHandler.java
│  │              │  │      ProviderUserInfo.java
│  │              │  │
│  │              │  ├─policy
│  │              │  │      ExpirationPolicy.java
│  │              │  │
│  │              │  └─token
│  │              │          JwtManager.java
│  │              │
│  │              ├─user
│  │              │  ├─controller
│  │              │  │      UserController.java
│  │              │  │
│  │              │  ├─dto
│  │              │  │      ImageResponse.java
│  │              │  │      NicknameRequest.java
│  │              │  │      NicknameResponse.java
│  │              │  │      UserDataResponse.java
│  │              │  │
│  │              │  ├─entity
│  │              │  │      SocialProvider.java
│  │              │  │      WebtyUser.java
│  │              │  │
│  │              │  ├─enumerate
│  │              │  │      SocialProviderType.java
│  │              │  │
│  │              │  ├─repository
│  │              │  │      SocialProviderRepository.java
│  │              │  │      UserRepository.java
│  │              │  │
│  │              │  └─service
│  │              │          UserService.java
│  │              │
│  │              ├─voting
│  │              │  ├─controller
│  │              │  │      SimilarController.java
│  │              │  │      VoteController.java
│  │              │  │
│  │              │  ├─dto
│  │              │  │      SimilarRequest.java
│  │              │  │      SimilarResponse.java
│  │              │  │      SimilarResponseRequest.java
│  │              │  │      VoteRequest.java
│  │              │  │
│  │              │  ├─entity
│  │              │  │      Similar.java
│  │              │  │      Vote.java
│  │              │  │
│  │              │  ├─enumerate
│  │              │  │      VoteType.java
│  │              │  │
│  │              │  ├─mapper
│  │              │  │      SimilarMapper.java
│  │              │  │      VoteMapper.java
│  │              │  │
│  │              │  ├─repository
│  │              │  │      SimilarRepository.java
│  │              │  │      VoteRepository.java
│  │              │  │
│  │              │  └─service
│  │              │          SimilarService.java
│  │              │          VoteService.java
│  │              │
│  │              └─webtoon
│  │                  ├─api
│  │                  │      WebtoonApiResponse.java
│  │                  │      WebtoonPageApiResponse.java
│  │                  │
│  │                  ├─controller
│  │                  │      FavoriteController.java
│  │                  │      WebtoonController.java
│  │                  │
│  │                  ├─dto
│  │                  │      FavoriteDto.java
│  │                  │      WebtoonDetailDto.java
│  │                  │      WebtoonSearchRequest.java
│  │                  │      WebtoonSummaryDto.java
│  │                  │
│  │                  ├─entity
│  │                  │      Favorite.java
│  │                  │      Webtoon.java
│  │                  │
│  │                  ├─enumerate
│  │                  │      Platform.java
│  │                  │      WebtoonSort.java
│  │                  │
│  │                  ├─mapper
│  │                  │      FavoriteMapper.java
│  │                  │      WebtoonApiResponseMapper.java
│  │                  │      WebtoonDetailMapper.java
│  │                  │
│  │                  ├─repository
│  │                  │      FavoriteRepository.java
│  │                  │      WebtoonRepository.java
│  │                  │
│  │                  └─service
│  │                          FavoriteService.java
│  │                          WebtoonService.java
│  │
│  └─resources
│      │  application-prod.yml
│      │  application-test.yml
│      │  application.yml
│      │
│      └─docs
│             application.md
│             naver-intellij-formatter.xml
│
└─test
    └─java
        └─org
            └─team14
                └─webty
                    │  WebtyApplicationTests.java
                    │
                    ├─review
                    │  └─controller
                    │          RecommendControllerTest.java
                    │          ReviewControllerTest.java
                    │
                    ├─security
                    │  ├─oauth2
                    │  │      LogoutSuccessHandlerTest.java
                    │  │
                    │  └─token
                    │          JwtManagerTest.java
                    │
                    ├─user
                    │  └─controller
                    │          UserControllerTest.java
                    │
                    ├─voting
                    │  └─controller
                    │          VoteControllerTest.java
                    │
                    └─webtoon
                        └─controller
                                FavoriteControllerTest.java
```

