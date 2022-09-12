package com.github.adrianlegui.challengebackendspring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.mappers.SerieMapper;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@Service
public class SerieService {
	@Autowired
	private PersonajeRepository personajeRepository;

	@Autowired
	private PeliculaRepository peliculaRepository;
	
	@Autowired
	private SerieRepository serieRepository;
	
	@Autowired
	SerieMapper serieMapper;
	
	public SerieDTO create(SerieDTOPOST serieDTOPOST) {
		return null;
	}
	
	public SerieDTOGET findById(Long id) {
		return null;
	}
	
	public List<PeliculaDTOGET> findAll(String nombre, Long idGenero, String orden){
		return null;
	}
	
	public SerieDTO update(SerieDTOPATCH serieDTOPATCH) {
		return null;
	}
	
	public void deleteById(Long id) {
		
	}
	
	public void deleteAll() {
		
	}
	
	public SerieDTO asociarPersonajeASerie(Long idSerie, Long idPersonaje) {
		return null;
	}
	
	public void desasociarPersonajeASerie(Long idSerie, Long idPersonaje) {
		
	}
}
