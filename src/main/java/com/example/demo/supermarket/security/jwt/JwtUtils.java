
package com.example.demo.supermarket.security.jwt;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.demo.supermarket.payload.response.JwtResponse;
import com.example.demo.supermarket.payload.response.UserValues;
import com.example.demo.supermarket.security.services.AuthenticationServiceImpl;
import com.example.demo.supermarket.security.services.UserDetailsImpl;
import com.example.demo.supermarket.security.services.UserDetailsServiceImpl;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${bezkoder.app.jwtSecret}")
	private String jwtSecret;

	@Value("${bezkoder.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	public AuthTokenFilter authenticationJwtTokenFilter;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthenticationServiceImpl authenticationServiceImpl;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public ResponseEntity<?> generateDynamicJwtToken(HttpServletRequest request) {
		String jwt = authenticationJwtTokenFilter.parseJwt(request);
		if (jwt != null && validateJwtToken(jwt)) {
			String username = getUserNameFromJwtToken(jwt);
				String jwtTocken= Jwts.builder()
						.setSubject(username)
						.setIssuedAt(new Date())
						.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
						.signWith(SignatureAlgorithm.HS512, jwtSecret)
						.compact();
				return ResponseEntity.ok(new JwtResponse(jwtTocken,new UserValues(null, username, null, null)));
		}
		return ResponseEntity.badRequest().build();

		
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
