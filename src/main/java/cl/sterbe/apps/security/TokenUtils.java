package cl.sterbe.apps.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {

    private final static String ACCESO_TOKEN_SECRETO = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVjOWYzYWI2NzY2Mjg2NDYyNDY0YTczNCIsIm5hbWUiOiJSYW5keSIsImF2YXRhciI6Ii8vd3d3LmdyYXZhdGFyLmNvbS9hdmF0YXIvMTNhN2MyYzdkOGVkNTNkMDc2MzRkOGNlZWVkZjM0NTE_cz0yMDAmcj1wZyZkPW1tIiwiaWF0IjoxNTU0NTIxNjk1LCJleHAiOjE1NTQ1MjUyOTV9._SxRurShXS-SI3SE11z6nme9EoaD29T_DBFr8Qwngkg";
    private final static Long ACCESO_TOKEN_VALIDACION_SEGUNDOS = 2_592_000L;

    public static String crearToken(String nombre, String email){
        long expiracionTiempo = ACCESO_TOKEN_VALIDACION_SEGUNDOS * 1_000;
        Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);

        Map<String, Object> extra = new HashMap<>();
        extra.put("nombre", nombre);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expiracionFecha)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESO_TOKEN_SECRETO.getBytes()))
                .compact();
    }

    public static UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken(String token){
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESO_TOKEN_SECRETO.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();

            return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        }catch (JwtException e){
            return null;
        }
    }
}
