package com.authentication.gw.common.util;

import com.authentication.gw.common.model.SessionUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

import static com.authentication.gw.common.ServiceConst.ROLE_PREFIX;

public class JWTUtil {
    private JWTUtil() {
        throw new UnsupportedOperationException();
    }

    private static final String JWT_SIGN_KEY_STRING = "mysignkey";
    private static final Key JWT_SIGN_KEY = new SecretKeySpec(JWT_SIGN_KEY_STRING.getBytes(StandardCharsets.UTF_8),
        "HmacSHA512");
    private static final int EXPIRED = 30; // 30분 만료.

    public static String createToken(String userId, String role, List<String> authority) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", userId);
        claims.put("role", role);
        claims.put("auth", authority);

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MINUTE, EXPIRED);
        dt = c.getTime();

        return Jwts.builder()
                   .setClaims(claims)
                   .setExpiration(dt)
                   .signWith(SignatureAlgorithm.HS512, JWT_SIGN_KEY)
                   .compact();
    }

    public static String createRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", userId);
        claims.put("type", "refresh");

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MINUTE, EXPIRED * 48);
        dt = c.getTime();

        return Jwts.builder()
                   .setClaims(claims)
                   .setExpiration(dt)
                   .signWith(SignatureAlgorithm.HS512, JWT_SIGN_KEY)
                   .compact();
    }

    @SuppressWarnings("unchecked")
    public static SessionUserDetails checkToken(String token) {
        Jws<Claims> jwt = signAndParseJWT(token);
        if (jwt == null) return null;
        Claims token_body = jwt.getBody();

        List<String> auth_list = (List<String>) token_body.get("auth");
        String id = String.valueOf(token_body.get("id"));
        String role = String.valueOf(token_body.get("role"));
        auth_list.add(ROLE_PREFIX + role);

        Date expiration = token_body.getExpiration();
        Date now = new Date();
        long expiresInMs = expiration.getTime() - now.getTime();

        long expiredThreshold = (long) (EXPIRED * 0.2 * 60 * 1000); // 만료시간 20% 이내일 경우 갱신

        SessionUserDetails user = new SessionUserDetails(id, role, setAuthorities(auth_list));

        // 80%이상 만료 시 자동 재발급
        // 응답 헤더를 받은 사용자는 갱신 요청을 보내야한다.
        if (expiresInMs <= expiredThreshold) {
            user.setToken(createToken(id, role, auth_list));
        }

        return user;
    }

    public static Jws<Claims> signAndParseJWT(String token) {
        try {
            return Jwts.parser()
                       .setSigningKey(JWT_SIGN_KEY)
                       .parseClaimsJws(token);
        } catch (Exception e) {
            // ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException,
            // IllegalArgumentException
            return null;
        }
    }

    // withoutSignature
    public static Claims parseJWT(String token) {
        try {
            int i = token.lastIndexOf('.');
            String withoutSignature = token.substring(0, i + 1);
            return Jwts.parser().parseClaimsJwt(withoutSignature).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static Collection<GrantedAuthority> setAuthorities(List<String> auth_list) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        auth_list.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));

        return authorities;
    }
}
