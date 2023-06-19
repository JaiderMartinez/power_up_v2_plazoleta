package com.reto.plazoleta.infraestructure.configuration.security.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.reto.plazoleta.infraestructure.configuration.security.jwt.exceptions.AuthenticationFailedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final IUserVerifierToken userVerifierToken;

    @Value("${access.token.secret}")
    private String accessTokenSecret;

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            String email = claims.getSubject();
            List<String> rol = claims.getStringListClaim("rol");
            return new UsernamePasswordAuthenticationToken(email, null,
                    rol.stream().map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));
        } catch (ParseException e) {
            throw new AuthenticationFailedException();
        }
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.accessTokenSecret.getBytes()).build().parseClaimsJws(token );
            return userVerifierToken.isValidTokenUser("Bearer " + token);
        } catch (MalformedJwtException | UnsupportedJwtException | ExpiredJwtException | IllegalArgumentException |
                SignatureException | DecodingException e) {
            return false;
        }
    }

}
