package com.github.adrianlegui.challengebackendspring.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SerieDTOPATCH {
	private Long id;
	private String titulo;
	private String imagen;
	private Integer calificacion;
	private LocalDate fechaDeCreacion;
}
