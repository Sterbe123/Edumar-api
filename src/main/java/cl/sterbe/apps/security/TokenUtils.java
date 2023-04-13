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

    public static String crearToken(String nombre, String email, List<String> roles){
        long expiracionTiempo = ACCESO_TOKEN_VALIDACION_SEGUNDOS * 1_000;
        Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);

        Map<String, Object> extra = new HashMap<>();
        extra.put("nombre", nombre);
        extra.put("authorities", roles);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expiracionFecha)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESO_TOKEN_SECRETO.getBytes()))
                .compact();
    }

    public static UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken(String token){
        List<GrantedAuthority> authorities = new ArrayList<>();

        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESO_TOKEN_SECRETO.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            claims.values().stream().forEach(s -> {
                if(s.toString().equals("[[ROLE_ADMINISTRADOR]]")){
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
                }else if(s.toString().equals("[[ROLE_TRABAJADOR]]")){
                    authorities.add(new SimpleGrantedAuthority("ROLE_TRABAJADOR"));
                }else if(s.toString().equals("[[ROLE_CLIENTE]]")){
                    authorities.add(new SimpleGrantedAuthority("ROLE_CLIENTE"));
                }
            });

            return new UsernamePasswordAuthenticationToken(email, null, authorities);
        }catch (JwtException e){
            return null;
        }
    }
}
