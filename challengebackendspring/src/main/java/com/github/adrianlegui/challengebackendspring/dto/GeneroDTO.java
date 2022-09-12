package com.github.adrianlegui.challengebackendspring.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GeneroDTO {
	private Long id;
	String nombre;
	String imagen;
	private List<PeliculaDTO> peliculas  = new ArrayList<>();
	private List<SerieDTO> series  = new ArrayList<>();
}
