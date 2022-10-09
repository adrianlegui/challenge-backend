package com.github.adrianlegui.challengebackendspring.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrianlegui.challengebackendspring.dto.LoginDTORequest;
import com.github.adrianlegui.challengebackendspring.dto.LoginDTOResponse;
import com.github.adrianlegui.challengebackendspring.dto.RegisterDTORequest;
import com.github.adrianlegui.challengebackendspring.dto.RegisterDTOResponse;
import com.github.adrianlegui.challengebackendspring.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;


	// register
	@PostMapping("/register")
	public ResponseEntity<RegisterDTOResponse> register(
		@Valid @RequestBody RegisterDTORequest registerDTORequest) {
		return new ResponseEntity<>(
			authService.registerUser(registerDTORequest),
			HttpStatus.CREATED);
	}


	// login
	@GetMapping("/login")
	public ResponseEntity<LoginDTOResponse> login(
		@Valid @RequestBody LoginDTORequest loginDTORequest) {
		return ResponseEntity
			.ok(authService.authenticateUser(loginDTORequest));
	}

}