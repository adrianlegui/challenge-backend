package com.github.adrianlegui.challengebackendspring.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTO;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPOST;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOId;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;
import com.github.adrianlegui.challengebackendspring.entities.PeliculaEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.AssociationNotExistingException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteNotExistedException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteVoidTableException;
import com.github.adrianlegui.challengebackendspring.exceptions.EntityNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.IdNullException;
import com.github.adrianlegui.challengebackendspring.mappers.PeliculaMapper;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;

@Service
@Transactional
public class PeliculaService {
	private static final String PELICULA_NOT_FOUND_WITH_ID = "pelicula not found with id ";

	@Autowired
	private PersonajeRepository personajeRepository;

	@Autowired
	private PeliculaRepository peliculaRepository;
	
	@Autowired
	private GeneroRepository generoRepository;
	
	@Autowired
	private PeliculaMapper peliculaMapper;
	
	public PeliculaDTO create(PeliculaDTOPOST peliculaDTOPOST) {
		// comprobar que existen los personajes
		for(PersonajeDTOId personaje : peliculaDTOPOST.getPersonajesEnPelicula())
			if(!personajeRepository.existsById(personaje.getId()))
				throw new EntityNotFoundException("personaje not found with id " + personaje.getId());
		
		// comprobar que existen los generos
		for(GeneroDTOId genero : peliculaDTOPOST.getGenerosDeLaPelicula())
			if(!generoRepository.existsById(genero.getId()))
				throw new EntityNotFoundException("genero not found with id " + genero.getId());
		
		// crear película sin asociaciones
		PeliculaDTOPATCH peliculaDTOPATCH = peliculaMapper.dtoToDtoPatch(peliculaDTOPOST);
		PeliculaEntity peliculaParaCrear = peliculaMapper.dtoPatchToEntity(peliculaDTOPATCH);
		PeliculaEntity peliculaCreada = peliculaRepository.save(peliculaParaCrear);
		
		// asociar personajes
		for(PersonajeDTOId personaje : peliculaDTOPOST.getPersonajesEnPelicula()) {
			Optional<PersonajeEntity> personajeOptional = personajeRepository.findById(personaje.getId());
			
			if(personajeOptional.isPresent()) {
				peliculaCreada.addPersonaje(personajeOptional.get());
				peliculaCreada = peliculaRepository.save(peliculaCreada);
			}
		}
		
		// asociar generos
		for(GeneroDTOId genero : peliculaDTOPOST.getGenerosDeLaPelicula()) {
			Optional<GeneroEntity> generoOptional = generoRepository.findById(genero.getId());
			
			if(generoOptional.isPresent()) {
				peliculaCreada.addGenero(generoOptional.get());
				peliculaCreada = peliculaRepository.save(peliculaCreada);
			}
		}
		
		return peliculaMapper.entityToDTO(peliculaCreada);
	}
	
	public PeliculaDTOGET findById(Long id) {
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(id);
		
		PeliculaEntity peliculaEntity;
		
		if(peliculaOptional.isPresent())
			peliculaEntity = peliculaOptional.get();
		else
			throw new EntityNotFoundException(PELICULA_NOT_FOUND_WITH_ID + id);
		
		return peliculaMapper.entityToDtoGet(peliculaEntity);
	}
	
	public List<PeliculaDTOGET> findAll(
			String nombre,
			Long idGenero,
			Orden orden){
		
		List<PeliculaDTOGET> peliculaDTOGETs = new ArrayList<>();
		
		List<PeliculaEntity> peliculaEntities = peliculaRepository.findAll();
		
		if(!peliculaEntities.isEmpty()) {
			Iterator<PeliculaEntity> iterator = peliculaEntities.iterator();
		
			while(iterator.hasNext()) {
				PeliculaEntity peliculaEntity = iterator.next();
				
				if((nombre != null && !nombre.equals(peliculaEntity.getTitulo()))
					|| (idGenero != null && !peliculaEntity.hasGeneroWithIdNumber(idGenero)))
					iterator.remove();
	
			}
				
			if(orden != null) {
				peliculaEntities.sort(Comparator.comparing(PeliculaEntity::getFechaDeCreacion));
				
				if(orden == Orden.DESC)
					Collections.reverse(peliculaEntities);
			}
			
			for(PeliculaEntity pelicula : peliculaEntities)
				peliculaDTOGETs.add(peliculaMapper.entityToDtoGet(pelicula));
		}
		
		return peliculaDTOGETs;
	}
	
	public PeliculaDTO update(PeliculaDTOPATCH peliculaDTOPATCH) {
		if(peliculaDTOPATCH.getId() == null)
			throw new IdNullException("can not uptate with id null");
		
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(peliculaDTOPATCH.getId());
		
		PeliculaEntity peliculaActualizada;
		
		if(peliculaOptional.isEmpty())
			throw new EntityNotFoundException(PELICULA_NOT_FOUND_WITH_ID + peliculaDTOPATCH.getId());
		else
			peliculaActualizada = peliculaMapper.dtoPatchToEntity(peliculaDTOPATCH, peliculaOptional.get());
		
		PeliculaEntity peliculaSalvada = peliculaRepository.save(peliculaActualizada);
		
		return peliculaMapper.entityToDTO(peliculaSalvada);
	}
	
	public void deleteById(Long id) {
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(id);
		
		if(peliculaOptional.isEmpty())
			throw new DeleteNotExistedException("pelicula not exist with id: " + id);
		else {
			PeliculaEntity peliculaEntity = peliculaOptional.get();
			
			for(PersonajeEntity personaje : peliculaEntity.getPersonajesEnPelicula()) {
				peliculaEntity.removePersonaje(personaje.getId());
				peliculaRepository.save(peliculaEntity);
			}
			
			for(GeneroEntity genero : peliculaEntity.getGenerosDeLaPelicula()) {
				peliculaEntity.removeGenero(genero.getId());
				peliculaRepository.save(peliculaEntity);
			}
			
			peliculaRepository.deleteById(id);
		}
	}
	
	public void deleteAll() {
		if(0 >= peliculaRepository.count())
			throw new DeleteVoidTableException("pelicula table is void");
		else {
			List<PeliculaEntity> peliculaEntities = peliculaRepository.findAll();
			
			for(PeliculaEntity pelicula : peliculaEntities)
				deleteById(pelicula.getId());
		}
	}
	
	public PeliculaDTO asociarPersonajeAPelicula(Long idPelicula, Long idPersonaje) {
		Optional<PeliculaEntity>  peliculaOptional = peliculaRepository.findById(idPelicula);
	
		if(peliculaOptional.isEmpty())
			throw new EntityNotFoundException(PELICULA_NOT_FOUND_WITH_ID + idPelicula);
		
		Optional<PersonajeEntity> personajeOptional = personajeRepository.findById(idPersonaje);
		
		if(personajeOptional.isEmpty())
			throw new EntityNotFoundException("personaje not found with id " + idPersonaje);
		 
		peliculaOptional.get().addPersonaje(personajeOptional.get());
		
		PeliculaEntity peliculaActualizada = peliculaRepository.save(peliculaOptional.get());
		
		return peliculaMapper.entityToDTO(peliculaActualizada);
	}
	
	public void desasociarPersonajeAPelicula(Long peliculaId, Long personajeId) {
		Optional<PeliculaEntity>  peliculaOptional = peliculaRepository.findById(peliculaId);
		
		if(peliculaOptional.isEmpty())
			throw new EntityNotFoundException(PELICULA_NOT_FOUND_WITH_ID + peliculaId);
		
		PeliculaEntity peliculaEntity = peliculaOptional.get();
		
		PersonajeEntity personajeEntity = peliculaEntity.getPersonajesEnPelicula().stream().filter(p -> Objects.equals(p.getId(), personajeId)).findFirst().orElse(null);
		
		if(personajeEntity == null)
			throw new AssociationNotExistingException(String.format("Association not existing between pelicula %s and personaje %s", peliculaId, personajeId));
		
		peliculaEntity.removePersonaje(personajeId);
		peliculaRepository.save(peliculaEntity);
	}
}
