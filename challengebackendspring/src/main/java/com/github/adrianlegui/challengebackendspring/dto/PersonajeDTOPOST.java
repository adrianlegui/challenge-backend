package com.github.adrianlegui.challengebackendspring.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PersonajeDTOPOST {
	private String imagen;
	private String nombre;
	private Integer edad;
	private Float peso;
	private String historia;
	private List<PeliculaDTOId> peliculas = new ArrayList<>();
	private List<SerieDTOId> series = new ArrayList<>();
}
