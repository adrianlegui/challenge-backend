package com.github.adrianlegui.challengebackendspring.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTO;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPOST;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOId;
import com.github.adrianlegui.challengebackendspring.entities.PeliculaEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteNotExistedException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteVoidTableException;
import com.github.adrianlegui.challengebackendspring.exceptions.EntityNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.IdNullException;
import com.github.adrianlegui.challengebackendspring.mappers.PersonajeMapper;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@Service
@Transactional
public class PersonajeService {
	@Autowired
	private PersonajeRepository personajeRepository;

	@Autowired
	private PeliculaRepository peliculaRepository;
	
	@Autowired
	private SerieRepository serieRepository;
	
	@Autowired
	private PersonajeMapper personajeMapper;
	
	
	public PersonajeDTO create(PersonajeDTOPOST personajeDTOPOST) {
		for(PeliculaDTOId dtoId : personajeDTOPOST.getPeliculas())
			if(!peliculaRepository.existsById(dtoId.getId()))
				throw new EntityNotFoundException("pelicula not found with id " + dtoId.getId());
		
		for(SerieDTOId dtoId : personajeDTOPOST.getSeries())
			if(!serieRepository.existsById(dtoId.getId()))
				throw new EntityNotFoundException("serie not found with id " + dtoId.getId());
		
		// crear personaje sin asociaciones
		PersonajeDTOPATCH personajeDTOPATCH = personajeMapper.dtoPostToDtoPatch(personajeDTOPOST);
		PersonajeEntity personajeParaCrear = personajeMapper.dtoPatchToEntity(personajeDTOPATCH);
		PersonajeEntity personajeCreado = personajeRepository.save(personajeParaCrear);
		
		// asociar películas
		for(PeliculaDTOId dtoId : personajeDTOPOST.getPeliculas()) {
			Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(dtoId.getId());
			
			if(peliculaOptional.isPresent()) {
				peliculaOptional.get().addPersonaje(personajeCreado);
				peliculaRepository.save(peliculaOptional.get());
			}
		}
		
		// asociar series
		for(SerieDTOId dtoId : personajeDTOPOST.getSeries()) {
			Optional<SerieEntity> serieOptional = serieRepository.findById(dtoId.getId());
			
			if(serieOptional.isPresent()) {
				serieOptional.get().addPersonaje(personajeCreado);
				serieRepository.save(serieOptional.get());
			}
		}
		
		PersonajeEntity personajeActualizado = new PersonajeEntity();
		Optional<PersonajeEntity> personajeOptional = personajeRepository.findById(personajeCreado.getId());
		
		if(personajeOptional.isPresent())
			personajeActualizado = personajeOptional.get();

		return personajeMapper.entityToDto(personajeActualizado);
	}
	
	
	public PersonajeDTOGET findById(Long id){
		Optional<PersonajeEntity> personajeOptional = personajeRepository.findById(id);
		
		PersonajeEntity personajeEntity;
		
		if(personajeOptional.isPresent())
			personajeEntity = personajeOptional.get();
		else
			throw new EntityNotFoundException("personaje not found with id " + id);
		
		return personajeMapper.entityToDtoGet(personajeEntity);
	}
	

	public List<PersonajeDTOGET> findAll(
			String nombre, 
			Integer edad, 
			Long idPelicula, 
			Long idSerie){
		List<PersonajeDTOGET> personajeDTOGETs = new ArrayList<>();
		
		List<PersonajeEntity> personajeEntities = personajeRepository.findAll();
		
		Iterator<PersonajeEntity> iterator;
		
		iterator = personajeEntities.iterator();
		
		while(iterator.hasNext()) {
			PersonajeEntity entity = iterator.next();
				
			if((nombre != null && !nombre.equals(entity.getNombre()))
				|| (edad != null && !Objects.equals(edad, entity.getEdad()))
				|| (idPelicula != null && !entity.hasMovieWithIdNumber(idPelicula))
				|| (idSerie != null && !entity.hasSerieWithIdNumber(idSerie)))
				iterator.remove();
		}
		
		for(PersonajeEntity entity : personajeEntities)
			personajeDTOGETs.add(personajeMapper.entityToDtoGet(entity));
		
		return personajeDTOGETs;
	}
	
	public PersonajeDTO update(PersonajeDTOPATCH personajeDTOPATCH) {
		if(personajeDTOPATCH.getId() == null)
			throw new IdNullException("can not uptate with id null") ;

		Optional<PersonajeEntity> personajeNoActualizadoOptional = personajeRepository.findById(personajeDTOPATCH.getId());
		
		PersonajeEntity personajeNoActualizado;
		
		if(personajeNoActualizadoOptional.isPresent())
			personajeNoActualizado = personajeNoActualizadoOptional.get();
		else
			throw new EntityNotFoundException("personaje not found with id " + personajeDTOPATCH.getId());
		
		PersonajeEntity personajeActualizado = personajeMapper.dtoPatchToEntity(personajeDTOPATCH, personajeNoActualizado);
		
		PersonajeEntity personajeSalvado = personajeRepository.save(personajeActualizado);
		
		return personajeMapper.entityToDto(personajeSalvado);
	}
	
	
	public void deleteById(Long id) {
		Optional<PersonajeEntity> personajeOptional = personajeRepository.findById(id);
		
		if(personajeOptional.isPresent()) {
			PersonajeEntity personaje = personajeOptional.get();
		
			for(PeliculaEntity pelicula : personaje.getPeliculas()) {
				pelicula.removePersonaje(id);
				peliculaRepository.save(pelicula);
			}
			
			for(SerieEntity serie : personaje.getSeries()) {
				serie.removePersonaje(id);
				serieRepository.save(serie);
			}
			
			personajeRepository.deleteById(id);
		} else
			throw new DeleteNotExistedException("personaje not exist with id: " + id);
	}
	
	
	public void deleteAll(){
		if(0 >= personajeRepository.count())
			throw new DeleteVoidTableException("personaje table is void");
		else {
			List<PersonajeEntity> personajeEntities = personajeRepository.findAll();
			
			for(PersonajeEntity personaje : personajeEntities)
				deleteById(personaje.getId());
		}
	}
}
