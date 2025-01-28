spring:
profiles:
active: prod
application:
name: webty

security:
oauth2:
client:
registration:
kakao:
client-id: ${OAUTH_KAKAO_CLIENT_ID}
client-secret: ${OAUTH_KAKAO_CLIENT_SECRET}
scope: profile_nickname
authorization-grant-type: authorization_code
redirect-uri: ${OAUTH_KAKAO_REDIRECT_URI}
client-name: Kakao
client-authentication-method: client_secret_post

        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id

jwt:
secret: V4r8jL3wqCz7oP7Y7Jl2kJcB9QxZlH4sRmGgqT9PZ8fF0e1yP1zDgT3zK7Qw3Fw
redirect: http://localhost:3000/callback
access-token:
expiration-time: 3600000
refresh-token:
expiration-time: 604800000