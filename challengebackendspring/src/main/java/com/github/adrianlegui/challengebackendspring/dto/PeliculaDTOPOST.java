package com.github.adrianlegui.challengebackendspring.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PeliculaDTOPOST {
	private String titulo;
	private LocalDate fechaDeCreacion;
	private Integer calificacion;
	private List<PersonajeDTOId> personajesEnPelicula = new ArrayList<>();
	private List<GeneroDTOId> generosDeLaPelicula = new ArrayList<>();
}
