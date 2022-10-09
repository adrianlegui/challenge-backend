package com.github.adrianlegui.challengebackendspring.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.github.adrianlegui.challengebackendspring.entities.Role;

import lombok.Data;

@Data
public class RegisterDTORequest {
	@NotBlank
	@Size(min = 5, max = 15)
	private String username;

	@NotBlank
	@Size(max = 60)
	@Email
	private String email;

	@Enumerated(EnumType.STRING)
	private Role role;

	@NotBlank
	@Size(min = 4, max = 40)
	private String password;
}
