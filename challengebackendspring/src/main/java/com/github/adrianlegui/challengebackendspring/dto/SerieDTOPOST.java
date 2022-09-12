package com.github.adrianlegui.challengebackendspring.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SerieDTOPOST {
	private String titulo;
	private String imagen;
	private Integer calificacion;
	private LocalDate fechaDeCreacion;
	private List<PersonajeDTOId> personajesEnSerie = new ArrayList<>();
	private List<GeneroDTOId> generosDeLaSerie = new ArrayList<>();
}
