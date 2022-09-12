package com.github.adrianlegui.challengebackendspring.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.AssociationNotExistingException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteNotExistedException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteVoidTableException;
import com.github.adrianlegui.challengebackendspring.exceptions.EntityNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.IdNullException;
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@SpringBootTest(classes = {MappersConfig.class})
@Transactional
class PeliculaServiceIntegrationTest {
	@Autowired
	PeliculaService peliculaService;
	
	@Autowired
	PeliculaRepository peliculaRepository;
	
	@Autowired
	PersonajeRepository personajeRepository;
	
	@Autowired
	SerieRepository serieRepository;
	
	@Autowired
	GeneroRepository generoRepository;
	
	@Test
	void create_WithNotExistPersonajeId_ThrowEntityNotFoundException() {
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		
		Long notFoundPersonajeId = 1L;
		Optional<PersonajeEntity> personajeOptional = personajeRepository.findById(notFoundPersonajeId);
		if(personajeOptional.isPresent()) {
			PersonajeEntity personajeEntity = personajeOptional.get();
			
			for(PeliculaEntity pelicula : personajeEntity.getPeliculas()) {
				pelicula.removePersonaje(notFoundPersonajeId);
				peliculaRepository.save(pelicula);
			}
			
			for(SerieEntity serie : personajeEntity.getSeries()) {
				serie.removePersonaje(notFoundPersonajeId);
				serieRepository.save(serie);
			}
			
			personajeRepository.deleteById(notFoundPersonajeId);
		}
		
		PersonajeDTOId personajeNotFound = new PersonajeDTOId();
		personajeNotFound.setId(notFoundPersonajeId);
		peliculaDTOPOST.getPersonajesEnPelicula().add(personajeNotFound);
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.create(peliculaDTOPOST));
	}
	
	@Test
	void create_WithNotExistGeneroId_ThrowEntityNotFoundException() {
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		
		Long notFoundGeneroId = 1L;
		
		Optional<GeneroEntity> generoOptional = generoRepository.findById(notFoundGeneroId);
		if(generoOptional.isPresent()) {
			GeneroEntity generoEntity = generoOptional.get();
			generoEntity.removeAllPelicula();
			generoEntity.removeAllSerie();
			generoRepository.save(generoEntity);
			
			generoRepository.deleteById(notFoundGeneroId);
		}
		
		GeneroDTOId generoNotFound = new GeneroDTOId();
		generoNotFound.setId(notFoundGeneroId);
		peliculaDTOPOST.getGenerosDeLaPelicula().add(generoNotFound);
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.create(peliculaDTOPOST));
	}
	
	@Test
	void create_WithPeliculaDTOPOST_ReturnPeliculaDTO() {
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		
		PersonajeEntity personajeEntity = personajeRepository.save(new PersonajeEntity());
		PersonajeDTOId personajeDTOId = new PersonajeDTOId();
		personajeDTOId.setId(personajeEntity.getId());
		peliculaDTOPOST.getPersonajesEnPelicula().add(personajeDTOId);
		
		GeneroEntity generoEntity = generoRepository.save(new GeneroEntity());
		GeneroDTOId generoDTOId = new GeneroDTOId();
		generoDTOId.setId(generoEntity.getId());
		peliculaDTOPOST.getGenerosDeLaPelicula().add(generoDTOId);
		
		PeliculaDTO resultado = peliculaService.create(peliculaDTOPOST);
		
		assertTrue(peliculaRepository.existsById(resultado.getId()));
		assertEquals(personajeEntity.getId(), resultado.getPersonajesEnPelicula().get(0).getId());
		assertEquals(generoEntity.getId(), resultado.getGenerosDeLaPelicula().get(0).getId());
	}
	
	@Test
	void findById_NotExistingPeliculaId_ThrowEntityNotFoundException() {
		Long notExistingPeliculaId = 1L;
		
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(notExistingPeliculaId);
		if(peliculaOptional.isPresent()){
			PeliculaEntity pelicula = peliculaOptional.get();
			
			for(PersonajeEntity personaje : pelicula.getPersonajesEnPelicula()){
				pelicula.removePersonaje(personaje.getId());
				peliculaRepository.save(pelicula);
			}
			
			for(GeneroEntity genero : pelicula.getGenerosDeLaPelicula()){
				pelicula.removeGenero(genero.getId());
				peliculaRepository.save(pelicula);
			}
			
			peliculaRepository.deleteById(notExistingPeliculaId);
		}
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.findById(notExistingPeliculaId));
	}
	
	@Test
	void findById_ExistingPeliculaId_ReturnPeliculaDTOGET() {
		Long existingPeliculaId = peliculaRepository.save(new PeliculaEntity()).getId();
		
		assertDoesNotThrow(() -> peliculaService.findById(existingPeliculaId));
	}
	
	@Test
	void findAll_ParamsNotNull_ReturnEmpty() {
		String nombre = "nombreEmpty";
		Long generoId = 1L;
		Orden orden = Orden.DESC;
		
		peliculaRepository.save(new PeliculaEntity());
		peliculaRepository.save(new PeliculaEntity());
		peliculaRepository.save(new PeliculaEntity());
		
		List<PeliculaDTOGET> resultado = peliculaService.findAll(nombre, generoId, orden);
		
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findAll_ParamsNull_ReturnAll() {
		peliculaRepository.save(new PeliculaEntity());
		peliculaRepository.save(new PeliculaEntity());
		peliculaRepository.save(new PeliculaEntity());
		
		List<PeliculaDTOGET> resultado = peliculaService.findAll(null, null, null);
		
		assertEquals(peliculaRepository.count(), resultado.size());
	}
	
	@Test
	void findAll_ParamsNotNull_ReturnOne() {
		String nombre = "nombreReturnOne";
		GeneroEntity generoEntity = generoRepository.save(new GeneroEntity());
		Long generoId = generoEntity.getId();
		Orden orden = Orden.DESC;
		
		peliculaRepository.save(new PeliculaEntity());
		peliculaRepository.save(new PeliculaEntity());
		PeliculaEntity peliculaEntity = peliculaRepository.save(new PeliculaEntity());
		peliculaEntity.setTitulo(nombre);
		peliculaEntity.addGenero(generoEntity);
		peliculaEntity = peliculaRepository.save(peliculaEntity);
		
		List<PeliculaDTOGET> resultado = peliculaService.findAll(nombre, generoId, orden);
		
		assertEquals(1, resultado.size());
		assertEquals(nombre, resultado.get(0).getTitulo());
	}
	
	@Test
	void findAll_ParamsNotNull_ReturnTwoOrderedDESC() {
		String nombre = "nombreTwoOrderedDESC";
		GeneroEntity generoEntity = generoRepository.save(new GeneroEntity());
		Long generoId = generoEntity.getId();
		Orden orden = Orden.DESC;
		
		peliculaRepository.save(new PeliculaEntity());
		
		PeliculaEntity peliculaEntity1 = peliculaRepository.save(new PeliculaEntity());
		peliculaEntity1.setTitulo(nombre);
		peliculaEntity1.addGenero(generoEntity);
		peliculaEntity1.setFechaDeCreacion(LocalDate.now());
		peliculaEntity1 = peliculaRepository.save(peliculaEntity1);
		
		PeliculaEntity peliculaEntity2 = peliculaRepository.save(new PeliculaEntity());
		peliculaEntity2.setTitulo(nombre);
		peliculaEntity2.addGenero(generoEntity);
		LocalDate localDate = LocalDate.now().plusDays(1L);
		peliculaEntity2.setFechaDeCreacion(localDate);
		peliculaEntity2 = peliculaRepository.save(peliculaEntity2);
		
		List<PeliculaDTOGET> resultado = peliculaService.findAll(nombre, generoId, orden);
		
		assertFalse(resultado.isEmpty());
		assertEquals(2, resultado.size());
		assertEquals(localDate, resultado.get(0).getFechaDeCreacion());
	}
	
	@Test
	void update_WithIdNull_throwIdNullException() {
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		assertThrows(
				IdNullException.class,
				() -> peliculaService.update(peliculaDTOPATCH)
				);
	}

	void update_WithNotExistingId_throwEntityNotFoundException() {
		Long notExistingId = 1L;
		
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(notExistingId);
		
		if(peliculaOptional.isPresent()) {
			PeliculaEntity pelicula = peliculaOptional.get();
			
			pelicula.removeAllGenero();
			pelicula.removeAllPersonaje();
			peliculaRepository.save(pelicula);
			
			peliculaRepository.deleteById(notExistingId);
		}
		
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		peliculaDTOPATCH.setId(notExistingId);
		
		assertThrows(
				EntityNotFoundException.class,
				() -> peliculaService.update(peliculaDTOPATCH)
				);
	}
	
	@Test
	void update_WithExistingId_ReturnPeliculaDTO() {
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		peliculaDTOPATCH.setId(pelicula.getId());
		peliculaDTOPATCH.setTitulo("nuevo titulo");
		
		PeliculaDTO resultado = peliculaService.update(peliculaDTOPATCH);
		
		assertEquals(
				peliculaDTOPATCH.getTitulo(),
				peliculaRepository.findById(pelicula.getId()).get().getTitulo()
				);
		
		assertEquals(
				peliculaDTOPATCH.getTitulo(),
				resultado.getTitulo());
	}
	
	@Test
	void deleteById_NotExistingId_ThrowDeleteNotExistedException() {
		Long notExistingId = 1L;
		
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(notExistingId);
		if(peliculaOptional.isPresent()) {
			PeliculaEntity pelicula = peliculaOptional.get();
			pelicula.removeAllGenero();
			pelicula.removeAllPersonaje();
			peliculaRepository.save(pelicula);
			peliculaRepository.deleteById(notExistingId);
		}
		
		assertThrows(
				DeleteNotExistedException.class, 
				() -> peliculaService.deleteById(notExistingId)
				);
	}
	
	@Test
	void deleteById_ExistingId_DoesNotThrowException() {
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		
		assertDoesNotThrow(() -> peliculaService.deleteById(pelicula.getId()));
		assertFalse(peliculaRepository.existsById(pelicula.getId()));
	}
	
	@Test
	void deleteAll_VoidTable_ThrowDeleteVoidTableException() {
		if(0 < peliculaRepository.count()) {
			List<PeliculaEntity> peliculaEntities = peliculaRepository.findAll();
			for(PeliculaEntity pelicula : peliculaEntities) {
				pelicula.removeAllGenero();
				pelicula.removeAllPersonaje();
				peliculaRepository.save(pelicula);
			}
			peliculaRepository.deleteAll();
		}
		
		assertThrows(
				DeleteVoidTableException.class, 
				() -> peliculaService.deleteAll()
				);
	}
	
	@Test
	void deleteAll_NotVoidTable_DoesNotThrowExceptions() {
		GeneroEntity genero = generoRepository.save(new GeneroEntity());
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		pelicula.addGenero(genero);
		pelicula.addPersonaje(personaje);
		peliculaRepository.save(pelicula);
		
		assertDoesNotThrow(() -> peliculaService.deleteAll());
		assertTrue(0 >= peliculaRepository.count());
	}
	
	@Test
	void asociarPersonajeApelicula_NotExistingPeliculaId_ThrowEntityNotFoundException() {
		Long notExistingId = 1L;
		
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(notExistingId);
		if(peliculaOptional.isPresent()) {
			PeliculaEntity pelicula = peliculaOptional.get();
			pelicula.removeAllGenero();
			pelicula.removeAllPersonaje();
			peliculaRepository.save(pelicula);
			peliculaRepository.deleteById(notExistingId);
		}
		
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		Long personajeId = personaje.getId();
		assertThrows(
				EntityNotFoundException.class,
				() -> peliculaService.asociarPersonajeAPelicula(
						notExistingId,
						personajeId)
				);
	}
	
	@Test
	void asociarPersonajeApelicula_NotExistingPersonajeId_ThrowEntityNotFoundException() {
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		Long notExistingPersonajeId = personaje.getId();
		personajeRepository.deleteById(notExistingPersonajeId);
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		Long peliculaId = pelicula.getId();
		
		assertThrows(
				EntityNotFoundException.class,
				() -> peliculaService.asociarPersonajeAPelicula(peliculaId, notExistingPersonajeId)
				);
	}
	
	@Test
	void asociarPersonajeAPelicula_ExistingIds_ReturnPeliculaDTO() {
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		Long personajeId = personaje.getId();
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		Long peliculaId = pelicula.getId();
		
		PeliculaDTO resultado = peliculaService.asociarPersonajeAPelicula(peliculaId, personajeId);
		
		assertEquals(personajeId, resultado.getPersonajesEnPelicula().get(0).getId());
	}
	
	@Test
	void desasociarPersonajeApelicula_NotExistingPeliculaId_ThrowEntityNotFoundException() {
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		Long notExistingPeliculaId = pelicula.getId();
		peliculaRepository.deleteById(notExistingPeliculaId);
		
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		Long personajeId = personaje.getId();
		
		assertThrows(
				EntityNotFoundException.class,
				() -> peliculaService.desasociarPersonajeAPelicula(
						notExistingPeliculaId,
						personajeId)
				);
	}
	
	@Test
	void desasociarPersonajeApelicula_NotExistingPersonajeId_ThrowAssociationNotExistingException() {
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		Long notExistingPersonajeId = personaje.getId();
		personajeRepository.deleteById(notExistingPersonajeId);
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		Long peliculaId = pelicula.getId();
		
		assertThrows(
				AssociationNotExistingException.class, 
				() -> peliculaService.desasociarPersonajeAPelicula(
						peliculaId,
						notExistingPersonajeId)
				);
	}
	
	@Test
	void desasociarPersonajeAPelicula_ExistingIds_DoesNotThrowException() {
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		Long personajeId = personaje.getId();
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		Long peliculaId = pelicula.getId();
		
		pelicula.addPersonaje(personaje);
		peliculaRepository.save(pelicula);
		
		assertDoesNotThrow(
				() -> peliculaService.desasociarPersonajeAPelicula(
						peliculaId,
						personajeId)
				);
		
		PeliculaEntity resultado = peliculaRepository.findById(peliculaId).get();
		
		assertTrue(resultado.getPersonajesEnPelicula().isEmpty());
	}
}
