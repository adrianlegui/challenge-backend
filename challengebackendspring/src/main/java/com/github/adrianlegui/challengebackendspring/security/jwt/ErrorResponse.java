package com.github.adrianlegui.challengebackendspring.security.jwt;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponse {
	private HttpStatus status;
	private String message;
	private String path;

}
