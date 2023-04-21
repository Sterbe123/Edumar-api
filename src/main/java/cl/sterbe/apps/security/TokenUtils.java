package cl.sterbe.apps.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class TokenUtils {

    private final static String ACCESO_TOKEN_SECRETO = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVjOWYzYWI2NzY2Mjg2NDYyNDY0YTczNCIsIm5hbWUiOiJSYW5keSIsImF2YXRhciI6Ii8vd3d3LmdyYXZhdGFyLmNvbS9hdmF0YXIvMTNhN2MyYzdkOGVkNTNkMDc2MzRkOGNlZWVkZjM0NTE_cz0yMDAmcj1wZyZkPW1tIiwiaWF0IjoxNTU0NTIxNjk1LCJleHAiOjE1NTQ1MjUyOTV9._SxRurShXS-SI3SE11z6nme9EoaD29T_DBFr8Qwngkg";
    private final static Long ACCESO_TOKEN_VALIDACION_SEGUNDOS = 2_592_000L;

    public static String crearToken(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        long expiracionTiempo = ACCESO_TOKEN_VALIDACION_SEGUNDOS * 1_000;
        Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);

        return Jwts.builder()
                .setId(String.valueOf(id))
                .setSubject(email)
                .claim("role", authorities.stream().findFirst().orElse(null).toString())
                .setExpiration(expiracionFecha)
                .signWith(Keys.hmacShaKeyFor(ACCESO_TOKEN_SECRETO.getBytes()))
                .compact();
    }

    public static UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken(String token) {

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESO_TOKEN_SECRETO.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new UsernamePasswordAuthenticationToken(claims.getSubject(),
                    null,
                    Arrays.asList(new SimpleGrantedAuthority(claims.get("role").toString())));
        } catch (JwtException e) {
            return null;
        }
    }
}
