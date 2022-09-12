package com.github.adrianlegui.challengebackendspring.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PeliculaDTO {
	private Long id;
	private String titulo;
	private LocalDate fechaDeCreacion;
	private Integer calificacion;
	private List<PersonajeDTOPATCH> personajesEnPelicula = new ArrayList<>();
	private List<GeneroDTOPATCH> generosDeLaPelicula = new ArrayList<>();
}
