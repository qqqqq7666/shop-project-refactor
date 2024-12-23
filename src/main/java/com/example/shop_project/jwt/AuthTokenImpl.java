package com.example.shop_project.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.example.shop_project.member.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.MalformedJwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


@Getter
@AllArgsConstructor
@Slf4j
public class AuthTokenImpl implements AuthToken<Claims> {
	private final String token;
	private final Key key;
	
	public AuthTokenImpl (
			String userId,
			Role role,
			Key key,
			Claims claims,
			Date expiredDate
	) {
		this.key= key;
		this.token = createJwtToken(userId, role, claims, expiredDate).get();
	}
	
	private Optional<String> createJwtToken(
			String userId,
			Role role,
			Map<String, Object> claimsMap,
			Date expiredDate
	) {
		DefaultClaims claims = new DefaultClaims(claimsMap);
		claims.put(AUTHORITIES_TOKEN_KEY, role);
		
		return Optional.ofNullable(Jwts.builder()
				.setSubject(userId)
				.addClaims(claims)
				.signWith(key, SignatureAlgorithm.HS256)
				.setExpiration(expiredDate)
				.compact()
		
		);
	}
	
	@Override
	public boolean validate() {
		return getDate() != null;
	}
	
	@Override
	public Claims getDate() {
		try {
			return Jwts
					.parserBuilder()
					.setSigningKey(key.getEncoded())
					.build()
					.parseClaimsJws(token)
					.getBody();
		} catch (SecurityException e) {
	        log.warn("Invalid JWT signature");
	    } catch (MalformedJwtException e) {
	        log.warn("Invalid JWT token");
	    } catch (ExpiredJwtException e) {
	        log.warn("Expired JWT token");
	    } catch (UnsupportedJwtException e) {
	        log.warn("Unsupported JWT Token");
	    } catch (IllegalArgumentException e) {
	        log.warn("JWT token compact of handler are invalid");
	    }
		
		return null;
	}
	
}
