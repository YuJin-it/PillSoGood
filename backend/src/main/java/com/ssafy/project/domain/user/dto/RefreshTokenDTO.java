package com.ssafy.project.domain.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RefreshTokenDTO {
    private String refreshToken;
}
