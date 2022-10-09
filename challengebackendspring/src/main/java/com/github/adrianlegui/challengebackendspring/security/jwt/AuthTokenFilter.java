package com.github.adrianlegui.challengebackendspring.security.jwt;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adrianlegui.challengebackendspring.services.CustomUserDetailsService;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private CustomUserDetailsService detailsService;


	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {

		Optional<String> tokenOptional = getTokenJwt(request);

		if (tokenOptional.isPresent()) {
			try {

				DecodedJWT decodedJWT =
					jwtUtils.validateToken(tokenOptional.get());

				UserDetails userDetails =
					detailsService
						.loadUserByUsername(
							decodedJWT.getSubject());

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
						userDetails, null,
						userDetails.getAuthorities());

				authentication
					.setDetails(
						new WebAuthenticationDetailsSource()
							.buildDetails(request));

				SecurityContextHolder
					.getContext()
					.setAuthentication(authentication);

			} catch (JWTVerificationException
				| UsernameNotFoundException e) {
				generateResponseException(
					request,
					response);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}


	private Optional<String> getTokenJwt(
		HttpServletRequest request) {
		String requestTokenHeader =
			request.getHeader("Authorization");

		if (StringUtils.hasText(requestTokenHeader)
			&& requestTokenHeader.startsWith("Bearer ")) {
			String tokenWithoutBearer =
				requestTokenHeader.split(" ")[1].trim();

			return Optional.of(tokenWithoutBearer);
		}

		return Optional.empty();
	}


	private void generateResponseException(
		HttpServletRequest request,
		HttpServletResponse response) throws IOException {

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		ErrorResponse apiError = new ErrorResponse();
		apiError.setStatus(HttpStatus.UNAUTHORIZED);
		apiError
			.setMessage(
				"User Unauthorized: Invalid token provided");
		apiError.setPath(request.getRequestURI());

		byte[] body =
			new ObjectMapper().writeValueAsBytes(apiError);

		response.getOutputStream().write(body);
	}
}
