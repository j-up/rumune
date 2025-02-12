package com.rumune.web.domain.user.application

import com.rumune.web.domain.cart.entity.Cart
import com.rumune.web.domain.cart.repository.CartRepository
import com.rumune.web.domain.user.entity.*
import com.rumune.web.domain.user.repository.UserRepository
import com.rumune.web.global.exception.OAuth2AlreadyExistException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class CustomOAuth2UserService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository,
): DefaultOAuth2UserService() {
    /**
     * 유저 정보 가져옴
     * CASE
     * 유저가 존재함 : 유저의 권한 확인
     * 유저가 존재하지 않음 : 신규 가입
     */
    @Override
    override fun loadUser(request: OAuth2UserRequest):OAuth2User {
        val userInfo: OAuth2UserInfo?
        val oAuth2User:OAuth2User = super.loadUser(request)
        val provider = request.clientRegistration.registrationId
        userInfo = when(provider.uppercase()) {
            "GOOGLE" -> GoogleUserInfo(oAuth2User.attributes)
            "KAKAO" -> KakaoUserInfo(oAuth2User.attributes)
            "DISCORD" -> DiscordUserInfo(oAuth2User.attributes)
            "NAVER" -> NaverUserInfo(oAuth2User.attributes)
            else -> null
        }
        if(userInfo == null) return oAuth2User
        val userOptional = userRepository.findByEmail(userInfo.getEmail())
        val authorities:Set<SimpleGrantedAuthority>
        if (userOptional.isEmpty) {
            val user = userRepository.save(
                User(
                    provider = userInfo.getProvider(),
                    email = userInfo.getEmail(),
                    pwd = "",
                    profileImage = userInfo.getProfileImage(),
                    providerId = userInfo.getId(),
                    name = userInfo.getName()
                )
            )
            cartRepository.save(
                Cart(user = user)
            )
            userService.addAuthority(userInfo.getEmail(),"ROLE_USER")
            authorities = HashSet(listOf(SimpleGrantedAuthority("ROLE_USER")))
        } else if (userOptional.get().provider != provider) {
            throw OAuth2AlreadyExistException("이미 다른 서비스를 통해 가입 내역이 있는 이메일입니다.", userOptional.get().provider, userOptional.get().email)
        } else {
            authorities = userOptional.get().authorities.map{SimpleGrantedAuthority(it.name)}.toSet()
        }
        return DefaultOAuth2User(authorities ?: listOf(), userInfo.getAttributes(), "email")
    }
}