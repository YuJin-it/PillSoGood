package com.ssafy.project.domain.user.controller;

import com.ssafy.project.domain.user.dto.UserDto;
import com.ssafy.project.domain.user.service.KakaoService;
import com.ssafy.project.domain.user.service.UserLoginService;
import com.ssafy.project.global.auth.JwtResponse;
import com.ssafy.project.global.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "카카오 로그인")
@RestController
@RequestMapping(value = "/api")
public class UserController {
    private final KakaoService kakaoService;
    private final UserLoginService userLoginService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(KakaoService kakaoService, UserLoginService userLoginService, JwtUtil jwtUtil) {
        this.kakaoService = kakaoService;
        this.userLoginService = userLoginService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "카카오 로그인")
    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody String kakaoToken) {
        if (kakaoToken == null || kakaoToken.isEmpty()) {
            // 유효하지 않은 카카오 토큰에 대해 예외 던짐
            throw new IllegalArgumentException("유효하지 않은 카카오 토큰");
        }

        // 카카오 사용자 정보 조회
        UserDto userInfo = kakaoService.getUserInfo(kakaoToken);

        // 사용자 정보 확인 및 신규 유저라면 가입 처리
        UserDto user = userLoginService.findOrCreateUser(userInfo);

        // JWT 토큰 발급
        String accessToken = jwtUtil.createAccessToken(user.getId());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());
        userLoginService.storeRefreshToken(user.getId(), refreshToken);

        // JWT 토큰 반환
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }

    @Operation(summary = "액세스 토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody String refreshToken) {
        if (jwtUtil.validateToken(refreshToken, true)) {
            int userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = jwtUtil.createAccessToken(userId);
            return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken));
        } else {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰");
        }
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        int userId = (Integer) request.getAttribute("userId");
        if (userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID");
        }

        userLoginService.invalidateRefreshToken(userId);
        return ResponseEntity.ok("로그아웃 성공");
    }
}