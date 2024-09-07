package org.dorandoran.dorandoran_backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.util.Date;
import java.security.Key;
import java.util.Base64;

public class JwtUtil {

    // 비밀 키를 직접 생성합니다. 이 방법이 가장 안전합니다.
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 3600000; // 토큰 만료 시간 (1시간)

    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String extractUserId(String token) {
        try {
            // JWT를 파싱하고 Claims 객체를 추출합니다.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 'subject' 필드에서 userId를 추출합니다.
            return claims.getSubject();
        } catch (SignatureException e) {
            // 서명 검증 실패
            throw new RuntimeException("Invalid JWT signature.");
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Invalid JWT token.");
        }
    }
}