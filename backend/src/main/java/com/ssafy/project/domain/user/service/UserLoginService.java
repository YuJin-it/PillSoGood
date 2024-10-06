package com.ssafy.project.domain.user.service;

import com.ssafy.project.domain.user.dto.UserDto;
import com.ssafy.project.domain.user.entity.User;
import com.ssafy.project.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserLoginService {
    private final UserRepository userRepository;

    @Autowired
    public UserLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto findOrCreateUser(UserDto userDto) {
        String kakaoKey = userDto.getKakaoKey();

        Optional<User> existingUser = userRepository.findByKakaoKey(kakaoKey);

        if (existingUser.isPresent()) {
            UserDto user = toDto(existingUser.get());
            user.setNewUser(false);
            return user;
        } else {
            User newUser = User.builder()
                    .kakaoKey(kakaoKey)
                    .email(userDto.getEmail())
                    .name(userDto.getName())
                    .flag(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User savedUser = userRepository.save(newUser);
            UserDto user = toDto(savedUser);
            user.setNewUser(true);
            return user;
        }
    }

    public void storeRefreshToken(int userId, String token) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRefreshToken(token);  // 리프레시 토큰 저장
            userRepository.save(user);  // 엔티티 업데이트
        } else {
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }
    }

    public void invalidateRefreshToken(int userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRefreshToken(null);  // 리프레시 토큰 무효화
            userRepository.save(user);  // 엔티티 업데이트
        } else {
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }
    }

    public int getUserIdByRefreshToken(String refreshToken) {
        Optional<User> userOpt = userRepository.findByRefreshToken(refreshToken);
        if (userOpt.isPresent()) {
            return userOpt.get().getId();
        } else {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }
    }

    public String getRefreshTokenByUserId(int userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return userOpt.get().getRefreshToken();
        } else {
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }
    }

    // User 엔티티를 UserDto로 변환하는 메서드
    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .kakaoKey(user.getKakaoKey())
                .flag(user.isFlag())
                .refreshToken(user.getRefreshToken())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}