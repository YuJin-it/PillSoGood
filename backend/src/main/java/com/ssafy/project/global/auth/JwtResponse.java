package com.ssafy.project.global.auth;

import lombok.Getter;

@Getter
public class JwtResponse {
    private final String accessToken;
    private final String refreshToken;

    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
