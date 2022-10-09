package com.github.adrianlegui.challengebackendspring.dto;

import java.util.Set;

import lombok.Data;

@Data
public class RegisterDTOResponse {
	private String username;
	private String email;
	private Set<RoleDTO> roles;
}
