package com.github.adrianlegui.challengebackendspring.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GeneroDTOPOST {
	String nombre;
	String imagen;
	private List<PeliculaDTOId> peliculas  = new ArrayList<>();
	private List<SerieDTO> series  = new ArrayList<>();
}
