package com.github.adrianlegui.challengebackendspring.dto;

import java.util.List;

import com.github.adrianlegui.challengebackendspring.entities.Role;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class LoginDTOResponse {
	private String token;
	@Setter(value = AccessLevel.NONE)
	private String type = "Bearer";
	private String username;
	private String email;
	private List<Role> roles;
}
