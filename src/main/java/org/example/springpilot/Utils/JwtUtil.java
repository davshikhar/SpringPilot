package org.example.springpilot.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

//    @Value("${secret_key}")
    private static final String secret_key = "Shikhar231sdhfksdhfkjshdfkjwerwet";

    private static final long Expiration_date = 1000*60*60;

    public String generateToken(UserDetails userDetails){
        //we send claims inside claims
        //example if there is additional information to be sent
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String,Object> claims, String username){
        return Jwts.builder().claims(claims)
                .subject(username)
                .header().empty().add("typ","JWT")
                .and().issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+Expiration_date))
                .signWith(getSignigKey())
                .compact();
    }

    private Key getSignigKey(){

        //here we convert the secret key so that we can put the key in the signWith() method
        return Keys.hmacShaKeyFor(secret_key.getBytes());
    }

    public boolean validToken(String token){
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSignigKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }
}
