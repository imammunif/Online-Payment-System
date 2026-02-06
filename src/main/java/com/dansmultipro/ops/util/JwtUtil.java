package com.dansmultipro.ops.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.sql.Timestamp;
import java.util.HashMap;

public class JwtUtil {

    public static String generateToken(String id, String roleCode, Timestamp timestamp) {
        var claims = new HashMap<String, Object>();
        claims.put("id", id);
        claims.put("roleCode", roleCode);
        claims.put("exp", timestamp);
        var secretKey =
                Keys.hmacShaKeyFor(Decoders.BASE64.decode("cffca86125ae36268b060f31ec1c90e028c08b0164d8ce3c4134829bfd6e9cc4"));
        var jwtBuilder =
                Jwts.builder().signWith(secretKey).setClaims(claims);
        return jwtBuilder.compact();
    }

    public static Claims validateToken(String token) {
        var secretKey =
                Keys.hmacShaKeyFor(Decoders.BASE64.decode("cffca86125ae36268b060f31ec1c90e028c08b0164d8ce3c4134829bfd6e9cc4"));
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token Expired");
        } catch (JwtException je) {
            throw new RuntimeException("Token Invalid");
        }
    }

}