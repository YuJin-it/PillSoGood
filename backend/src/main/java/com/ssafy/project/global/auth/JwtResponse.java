package com.ssafy.project.global.auth;

import lombok.Getter;

@Getter
public class JwtResponse {
    private final String accessToken;
    private final String refreshToken;
    private boolean isNewUser;

    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public JwtResponse(String accessToken, String refreshToken, boolean isNewUser) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewUser = isNewUser;
    }

}
