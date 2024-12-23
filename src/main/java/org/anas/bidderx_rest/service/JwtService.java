package org.anas.bidderx_rest.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.TokenResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SignatureException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${security.jwt.access-secret-key}")
    private String accessSecretKey;

    @Value("${security.jwt.refresh-secret-key}")
    private String refreshSecretKey;

    @Value("${security.jwt.access-expiration-time}")
    private long accessExpiration;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration;

    public String getRefreshSecretKey() {
        return refreshSecretKey;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessExpiration, accessSecretKey);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration, refreshSecretKey);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            String secretKey
    ) {
        AppUser appUser = (AppUser) userDetails;
        UUID userId = appUser.getId();

        List<String> roles = appUser.getRoles()
                .stream()
                .map(role -> "ROLE_" + role.name())
                .collect(Collectors.toList());

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim("id", userId.toString())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, String secretKey) {
        final String username = extractUsername(token, secretKey);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, secretKey));
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken, refreshSecretKey);
            Date now = new Date();

            // Check if token is not expired
            if (claims.getExpiration().before(now)) {
                return false;
            }

            // Validate subject is present
            if (claims.getSubject() == null || claims.getSubject().isEmpty()) {
                return false;
            }

            // Additional optional validations can be added here
            // For example, check for specific claims or token properties

            return true;
        } catch (ExpiredJwtException e) {
            // Token has expired
            return false;
        } catch (JwtException e) {
            // Covers signature exceptions, malformed tokens, etc.
            return false;
        }
    }

    public String extractUsername(String token, String secretKey) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    private Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey);
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//    public long getExpirationTime() {
//        return jwtExpiration;
//    }
}
