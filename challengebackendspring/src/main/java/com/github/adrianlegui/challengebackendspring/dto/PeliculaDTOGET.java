package com.github.adrianlegui.challengebackendspring.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PeliculaDTOGET {
	private Long id;
	private String titulo;
	private LocalDate fechaDeCreacion;
}
