package com.github.adrianlegui.challengebackendspring.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PersonajeDTO {
	private Long id;
	private String imagen;
	private String nombre;
	private Integer edad;
	private Float peso;
	private String historia;
	private List<PeliculaDTOPATCH> peliculas = new ArrayList<>();
	private List<SerieDTOPATCH> series = new ArrayList<>();

}
