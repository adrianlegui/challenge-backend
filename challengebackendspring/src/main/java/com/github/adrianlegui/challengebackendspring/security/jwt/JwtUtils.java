package com.github.adrianlegui.challengebackendspring.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.github.adrianlegui.challengebackendspring.security.CustomUserDetails;

@Component
public class JwtUtils {
	@Autowired
	Algorithm algorithm;

	String issuer = "challenger-spring";

	@Autowired
	JWTVerifier jwtVerifier;


	public String generateToken(Authentication authentication) {
		CustomUserDetails userDetails =
			(CustomUserDetails) authentication.getPrincipal();
		return JWT
			.create()
			.withIssuer(issuer)
			.withSubject(userDetails.getUsername())
			.sign(algorithm);
	}


	public DecodedJWT validateToken(String token) throws JWTDecodeException, JWTVerificationException{
		return jwtVerifier.verify(token);
	}

}
