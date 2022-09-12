package com.github.adrianlegui.challengebackendspring.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SerieDTO {
	private Long id;
	private String titulo;
	private String imagen;
	private Integer calificacion;
	private LocalDate fechaDeCreacion;
	private List<PersonajeDTOPATCH> personajesEnSerie = new ArrayList<>();
	private List<GeneroDTOPATCH> generosDeLaSerie = new ArrayList<>();
}
