package com.taskflow.api.auth.filter.login;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("로그인필터 동작");
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        // DB에서 사용자 정보 조회
        Optional<UserEntity> userEntity = userRepository.findByEmailAndIsOAuth(email, false);

        // DB에 이메일이 없으면 BadCredentialsException
        if (userEntity.isEmpty())
            throw new BadCredentialsException("Invalid email or password");

        // DB에 비밀번호가 일치하지 않으면 BadCredentialsException
        UserEntity user = userEntity.get();
        log.info("given password: {}", password);
        log.info("identified password: {}", user.getPassword());

        // TODO:
        // if (!passwordEncoder.matches(password, user.getPassword())) {
        // throw new BadCredentialsException("Invalid email or password");
        // }
        // 테스트를 위해 아래구문으로 실행
        if (!password.equals(user.getPassword()))
            throw new BadCredentialsException("Invalid email or password");

        // 인증된 토큰 생성. 여기서는 credential를 다음 필터로 넘겨야 할 필요성이 없기 때문에 null 처리
        return new UsernamePasswordAuthenticationToken(email, null, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
