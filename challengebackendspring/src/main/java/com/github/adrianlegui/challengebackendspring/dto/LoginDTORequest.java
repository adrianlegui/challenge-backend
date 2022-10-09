package com.github.adrianlegui.challengebackendspring.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginDTORequest {
	@NotBlank
	private String username;
	@NotBlank
	private String password;
}
