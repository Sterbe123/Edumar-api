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
    private final static Long ACCESO_TOKEN_VALIDACION_MINUTO = 60_000L;

    public static String crearToken(Long id, String email, Collection<? extends GrantedAuthority> authorities,
                                    boolean estado, boolean verificacion) {
        long expiracionTiempo = ACCESO_TOKEN_VALIDACION_MINUTO * 60;
        Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);

        Map<String, Object> estados = new HashMap<>();
        estados.put("estado", estado);
        estados.put("verificacion", verificacion);

        return Jwts.builder()
                .setId(String.valueOf(id))
                .setSubject(email)
                .claim("role", authorities.stream().findFirst().orElse(null).toString())
                .addClaims(estados)
                .setExpiration(expiracionFecha)
                .signWith(Keys.hmacShaKeyFor(ACCESO_TOKEN_SECRETO.getBytes()))
                .compact();
    }

    public static UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken(String token) {

        Map<String, Object> credenciales = new HashMap<>();

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESO_TOKEN_SECRETO.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            credenciales.put("estado", claims.get("estado").toString());
            credenciales.put("verificacion", claims.get("verificacion").toString());

             return new UsernamePasswordAuthenticationToken(claims.getSubject(),
                     credenciales,
                    Arrays.asList(new SimpleGrantedAuthority(claims.get("role").toString())));
        } catch (JwtException e) {
            return null;
        }
    }

    public static String crearTokenValidacionUsuario(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        long expiracionTiempo = ACCESO_TOKEN_VALIDACION_MINUTO * 5;
        Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);

        return Jwts.builder()
                .setId(String.valueOf(id))
                .setSubject(email)
                .claim("role", authorities.stream().findFirst().orElse(null).toString())
                .setExpiration(expiracionFecha)
                .signWith(Keys.hmacShaKeyFor(ACCESO_TOKEN_SECRETO.getBytes()))
                .compact();
    }

    public static Map<String, Object> verifyAuthenticationToken(String token) {
        Map<String, Object> mensajes = new HashMap<>();

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESO_TOKEN_SECRETO.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            mensajes.put("verificacion", true);
            mensajes.put("id", claims.getId());
            mensajes.put("email", claims.getSubject());
            mensajes.put("role", claims.get("role").toString());

            return mensajes;
        } catch (JwtException e) {
            mensajes.put("verificacion", false);
            return mensajes;
        }
    }
}
