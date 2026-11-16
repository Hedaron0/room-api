package com.dsu.roomapi;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
