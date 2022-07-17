package com.example.todo.config;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.example.todo.dto.TokenDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.todo.entity.User;
import com.example.todo.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private String secretKey = "123qwe";

    private final UserService userService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokenDto createTokenDto(String userPk, List<String> roles) {
        Date now = new Date();
        return new TokenDto(
                createAccessToken(userPk, roles, now),
                createRefreshToken(now)
        );
    }

    public String createAccessToken(String userPk, List<String> roles, Date date) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        long accessTokenValidTime = 30 * 60 * 1000L;
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(Date date) {
        long refreshTokenValidTime = 30 * 60 * 1000L;
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(new Date(date.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        User user = userService.retrieve(Integer.parseInt(this.getUserPk(token)));
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

//    public boolean validationToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return true;
//        } catch (SecurityException | MalformedJwtException e) {
//            log.error("잘못된 Jwt 서명입니다.");
//        } catch (ExpiredJwtException e) {
//            log.error("만료된 토큰입니다.");
//        } catch (UnsupportedJwtException e) {
//            log.error("지원하지 않는 토큰입니다.");
//        } catch (IllegalArgumentException e) {
//            log.error("잘못된 토큰입니다.");
//        }
//        return false;
//    }
}
