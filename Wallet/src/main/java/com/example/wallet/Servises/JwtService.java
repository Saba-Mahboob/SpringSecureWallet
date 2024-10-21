package com.example.wallet.Servises;

import com.example.wallet.Entities.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

         private static final String SECRET_KEY = "p6f3z8e7G+1H8MUpgkA9eU8wQ8wFkqW8yP1y/ZbFhM0=";
         private static final Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
         private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String extractPersonId(String token) {
         //System.out.println(" --"+extractAllClaims(token).getSubject());
        return (extractAllClaims(token).getSubject());
    }


        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            //System.out.println(claims);
            return claimsResolver.apply(claims);
        }

        public Claims extractAllClaims(String token) {
            //System.out.println(token);
            if (token == null || token.isEmpty() || token.split("\\.").length != 3) {
                throw new IllegalArgumentException("JWT token is invalid");
            }
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (Exception e) {
                logger.error("Error extracting claims from JWT: {}", e.getMessage());
                throw new RuntimeException("Invalid JWT token");
            }
        }

        public Boolean isTokenExpired(String token) {
            System.out.println("isTokenExpired");
            return extractExpiration(token).before(new Date());
        }

        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        public String generateToken(Person person) {
            Map<String, Object> claims = new HashMap<>();
            return createToken(claims, person.getId());
        }

        private String createToken(Map<String, Object> claims, String subject) {
           // System.out.println("subject is"+subject+".............");
            return Jwts.builder().
                    setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(key)
                    .compact();
        }

        public Boolean validateToken(String token, UserDetails userDetails) {
            final String id = extractPersonId(token);
            return (id.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }
    }

