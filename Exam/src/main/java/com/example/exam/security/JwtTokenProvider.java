package com.example.exam.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.validity-in-ms}") long validityInMs) {
        // 使用 Keys.hmacShaKeyFor 生成密钥
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMs = validityInMs;
        System.out.println("生成的密钥: " + new String(key.getEncoded(), StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())  // 0.12.x 使用 subject() 替代 setSubject()
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(new Date())  // 0.12.x 使用 issuedAt() 替代 setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + validityInMs))  // 0.12.x 使用 expiration() 替代 setExpiration()
                .signWith(key)  // 0.12.x 无需指定算法，自动推断
                .compact();
    }

    public boolean validateToken(String token) {
        // 去除 token 中的所有空格
        String processedToken = token.replaceAll("\\s+", "");
        // 打印原始的 token
        System.out.println("原始 Token: " + token);
        // 打印处理后的 token
        System.out.println("处理后 Token: " + processedToken);

        try {
            System.out.println("验证时使用的密钥: " + new String(key.getEncoded(), StandardCharsets.UTF_8));
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)  // 0.12.x 使用 verifyWith() 替代 setSigningKey()
                    .build()
                    .parseSignedClaims(processedToken);  // 0.12.x 使用 parseSignedClaims() 替代 parseClaimsJws()

            // 获取令牌的过期时间
            Date expiration = claimsJws.getPayload().getExpiration();
            // 获取当前时间
            Date now = new Date();

            // 检查令牌是否过期
            if (expiration != null && expiration.before(now)) {
                System.out.println("Token 已过期");
                return false;
            } else {
                System.out.println("Token 未过期");
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT 验证失败: " + e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()  // 0.12.x 使用 getPayload() 替代 getBody()
                .getSubject();
    }
}