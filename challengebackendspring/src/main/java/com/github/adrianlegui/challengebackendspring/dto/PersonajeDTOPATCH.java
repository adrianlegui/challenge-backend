package com.github.adrianlegui.challengebackendspring.dto;

import lombok.Data;

@Data
public class PersonajeDTOPATCH {
	private Long id;
	private String imagen;
	private String nombre;
	private Integer edad;
	private Float peso;
	private String historia;
}
