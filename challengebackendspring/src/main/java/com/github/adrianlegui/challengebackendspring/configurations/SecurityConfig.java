package com.github.adrianlegui.challengebackendspring.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.github.adrianlegui.challengebackendspring.security.jwt.AuthTokenFilter;
import com.github.adrianlegui.challengebackendspring.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	String issuer = "challenger-spring";

	@Autowired
	CustomUserDetailsService userDetailsService;


	@Bean
	public PasswordEncoder createDelegatingPasswordEncoder() {
		return PasswordEncoderFactories
			.createDelegatingPasswordEncoder();
	}


	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}


	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(
		PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider daoAuthenticationProvider =
			new DaoAuthenticationProvider();

		daoAuthenticationProvider
			.setUserDetailsService(userDetailsService);

		daoAuthenticationProvider
			.setPasswordEncoder(passwordEncoder);

		return daoAuthenticationProvider;
	}


	@Bean
	public SecurityFilterChain createSecurityFilterChain(
		HttpSecurity httpSecurity,
		DaoAuthenticationProvider authenticationProvider,
		AuthTokenFilter authTokenFilter) throws Exception {
		return httpSecurity
			.cors()
			.and()
			.csrf()
			.disable()
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(
				authTokenFilter,
				UsernamePasswordAuthenticationFilter.class)
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers("/auth/**")
			.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.build();
	}


	@Bean
	public Algorithm createAlgotihm() {
		return Algorithm.HMAC256("secretcodesign");
	}


	@Bean
	public JWTVerifier createVerifier(Algorithm algorithm) {
		return JWT.require(algorithm).withIssuer(issuer).build();
	}
}
