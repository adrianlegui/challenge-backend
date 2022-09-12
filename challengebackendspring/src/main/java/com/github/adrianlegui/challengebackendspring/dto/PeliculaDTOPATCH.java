package com.github.adrianlegui.challengebackendspring.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PeliculaDTOPATCH {
	private Long id;
	private String titulo;
	private LocalDate fechaDeCreacion;
	private Integer calificacion;
}
