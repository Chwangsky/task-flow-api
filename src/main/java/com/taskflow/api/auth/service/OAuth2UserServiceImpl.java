package com.taskflow.api.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.taskflow.api.auth.handler.CustomOAuth2User;
import com.taskflow.api.global.entity.UserEntity;
import com.taskflow.api.global.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(request);
        String provider = request.getClientRegistration().getClientName();

        // 유저 정보 초기화
        String userEmail = null;
        String userNickname = null;
        String userProfileImage = null;

        if ("Google".equals(provider)) {
            userEmail = (String) oAuth2User.getAttributes().get("email");
            Optional<UserEntity> existingUser = userRepository.findByEmailAndIsOAuth(userEmail, true);

            if (existingUser.isPresent()) {
                // 1. 이미 계정이 DB에 등록된 경우
                userNickname = existingUser.get().getNickname();
                userProfileImage = existingUser.get().getProfileImageUrl();
            } else {
                // 2. 계정이 DB에 없는 경우, 새 유저 생성
                userNickname = (String) oAuth2User.getAttributes().get("given_name");
                userNickname = getAlternativeUserNickname(userNickname); // 닉네임 중복 처리
                userProfileImage = (String) oAuth2User.getAttributes().get("picture");

                UserEntity newUserEntity = UserEntity.builder()
                        .email(userEmail)
                        .nickname(userNickname)
                        .profileImageUrl(userProfileImage)
                        .isOAuth(true)
                        .build();
                userRepository.save(newUserEntity); // 새 유저 저장
            }
        }

        return new CustomOAuth2User(userEmail, userNickname, userProfileImage, provider);
    }

    /**
     * Auth server에서 받아온 유저 닉네임이 DB에 존재하는지 찾아본다.
     * 만약 동일한 유저 닉네임이 이미 존재한다면, {닉네임}1, {닉네임}2.. 이런 식으로 반환한다.
     *
     * @param userNickname
     * @return alternative userNickname
     */
    private String getAlternativeUserNickname(String userNickname) {
        Optional<UserEntity> users = userRepository.findByNickname(userNickname);
        int tmpNumber = 0;
        while (users.isPresent()) {
            tmpNumber++;
            users = userRepository.findByNickname(userNickname + tmpNumber);
        }
        return tmpNumber == 0 ? userNickname : userNickname + tmpNumber;
    }
}
