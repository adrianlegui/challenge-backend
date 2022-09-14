package com.github.adrianlegui.challengebackendspring.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOId;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTO;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPOST;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPATCH;
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

@ExtendWith(MockitoExtension.class)
class PeliculaServiceTest {
	@InjectMocks
	PeliculaService peliculaService;
	
	@Mock
	PeliculaRepository peliculaRepository;
	
	@Mock
	PersonajeRepository personajeRepository;
	
	@Mock
	GeneroRepository generoRepository;
	
	@Mock
	PeliculaMapper peliculaMapper;
	
	// findAll test
	List<PeliculaEntity> peliculaEntities;
	PeliculaEntity peliculaEntity1;
	PeliculaEntity peliculaEntity2;
	PeliculaDTOGET peliculaDTOGET1;
	PeliculaDTOGET peliculaDTOGET2;
	// findAll test end
	
	@BeforeEach
	void setUp() throws Exception {
		// findAll test
		peliculaEntities = new ArrayList<>();
		
		peliculaEntity1 = new PeliculaEntity();
		peliculaEntity1.setFechaDeCreacion(LocalDate.now().plusDays(1L));
		peliculaDTOGET1 = new PeliculaDTOGET();
		peliculaDTOGET1.setFechaDeCreacion(LocalDate.now().plusDays(1L));
		
		peliculaEntity2 = new PeliculaEntity();
		peliculaEntity2.setFechaDeCreacion(LocalDate.now());
		peliculaDTOGET2 = new PeliculaDTOGET();
		peliculaDTOGET2.setFechaDeCreacion(LocalDate.now());
		// findAll test end
	}
	
	@Test
	void create_WithNotExistPersonajeId_ThrowEntityNotFoundException() {
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		
		Long idNotFound = 1L;
		PersonajeDTOId personajeNotFound = new PersonajeDTOId();
		personajeNotFound.setId(idNotFound);
		peliculaDTOPOST.getPersonajesEnPelicula().add(personajeNotFound);
		
		when(personajeRepository.existsById(idNotFound)).thenReturn(false);
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.create(peliculaDTOPOST));
	}
	
	@Test
	void create_WithNotExistGeneroId_ThrowEntityNotFoundException() {
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		
		Long idNotFound = 1L;
		GeneroDTOId generoNotFound = new GeneroDTOId();
		generoNotFound.setId(idNotFound);
		peliculaDTOPOST.getGenerosDeLaPelicula().add(generoNotFound);
		
		when(generoRepository.existsById(idNotFound)).thenReturn(false);
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.create(peliculaDTOPOST));
	}
	
	@Test
	void create_WithPeliculaDTOPOST_ReturnPeliculaDTO() {
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		
		Long idFound = 1L;
		PersonajeDTOId personajeFound = new PersonajeDTOId();
		personajeFound.setId(idFound);
		peliculaDTOPOST.getPersonajesEnPelicula().add(personajeFound);
		when(personajeRepository.existsById(idFound)).thenReturn(true);
		
		GeneroDTOId generoFound = new GeneroDTOId();
		generoFound.setId(idFound);
		peliculaDTOPOST.getGenerosDeLaPelicula().add(generoFound);
		when(generoRepository.existsById(idFound)).thenReturn(true);
		
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		peliculaDTOPATCH.setId(idFound);
		
		when(peliculaMapper.dtoPostToDtoPatch(peliculaDTOPOST)).thenReturn(peliculaDTOPATCH);
		
		PeliculaEntity peliculaParaCrear = new PeliculaEntity();
		peliculaParaCrear.setId(idFound);
		
		when(peliculaMapper.dtoPatchToEntity(peliculaDTOPATCH)).thenReturn(peliculaParaCrear);
		
		when(peliculaRepository.save(peliculaParaCrear)).thenReturn(peliculaParaCrear);
		
		PersonajeEntity personajeEnBD = new PersonajeEntity();
		personajeEnBD.setId(idFound);
		
		when(personajeRepository.findById(idFound)).thenReturn(Optional.of(personajeEnBD));
		
		peliculaParaCrear.addPersonaje(personajeEnBD);
		
		when(peliculaRepository.save(peliculaParaCrear)).thenReturn(peliculaParaCrear);
		
		GeneroEntity generoEnBD = new GeneroEntity();
		generoEnBD.setId(idFound);
		
		when(generoRepository.findById(idFound)).thenReturn(Optional.of(generoEnBD));
		
		peliculaParaCrear.addGenero(generoEnBD);
		
		when(peliculaRepository.save(peliculaParaCrear)).thenReturn(peliculaParaCrear);
		
		PeliculaDTO peliculaDTO = new PeliculaDTO();
		peliculaDTO.setId(idFound);
		
		GeneroDTOPATCH generoDTOPATCH = new GeneroDTOPATCH();
		generoDTOPATCH.setId(idFound);
		peliculaDTO.getGenerosDeLaPelicula().add(generoDTOPATCH);
		
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		personajeDTOPATCH.setId(idFound);
		peliculaDTO.getPersonajesEnPelicula().add(personajeDTOPATCH);
		
		when(peliculaMapper.entityToDTO(peliculaParaCrear)).thenReturn(peliculaDTO);
		
		assertDoesNotThrow(() -> peliculaService.create(peliculaDTOPOST));
	}
	
	@Test
	void findById_PeliculaIdNotExisting_ThrowEntityNotFoundException() {
		Long idNotExisting = 1L;
		
		when(peliculaRepository.findById(idNotExisting)).thenReturn(Optional.empty());
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.findById(idNotExisting));
	}
	
	@Test
	void findById_PeliculaIdExisting_ReturnPeliculaDTOGET() {
		Long idExisting = 1L;
		
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(idExisting);
		when(peliculaRepository.findById(idExisting)).thenReturn(Optional.of(peliculaEntity));
		
		PeliculaDTOGET peliculaDTOGET = new PeliculaDTOGET();
		peliculaDTOGET.setId(idExisting);
		when(peliculaMapper.entityToDtoGet(peliculaEntity)).thenReturn(peliculaDTOGET);
		
		assertDoesNotThrow(() -> peliculaService.findById(idExisting));
	}
	
	@Test
	void findAll_ParamsNotNull_ReturnEmpty() {
		when(peliculaRepository.findAll()).thenReturn(peliculaEntities);
		
		List<PeliculaDTOGET> resultado = peliculaService.findAll(null, null, null);
		
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findAll_ParamsNull_ReturnAll() {
		peliculaEntities.add(peliculaEntity1);
		peliculaEntities.add(peliculaEntity2);
		
		when(peliculaRepository.findAll()).thenReturn(peliculaEntities);

		List<PeliculaDTOGET> resultado = peliculaService.findAll(null, null, null);
		
		assertFalse(resultado.isEmpty());
		assertEquals(2, resultado.size());
	}
	
	@Test
	void findAll_ParamsNotNull_ReturnOne() {
		Long generoId = 1L;
		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.setId(generoId);
		peliculaEntity2.getGenerosDeLaPelicula().add(generoEntity);
		
		Long peliculaId = 1L;
		peliculaEntity2.setId(peliculaId);
		String nombre = "nombre";
		peliculaEntity2.setTitulo(nombre);
		
		peliculaEntities.add(peliculaEntity1);
		peliculaEntities.add(peliculaEntity2);
		
		when(peliculaRepository.findAll()).thenReturn(peliculaEntities);
		
		when(peliculaMapper.entityToDtoGet(peliculaEntity2)).thenReturn(peliculaDTOGET2);
		List<PeliculaDTOGET> resultado = peliculaService.findAll(nombre, generoId, null);
		
		assertFalse(resultado.isEmpty());
		assertEquals(1, resultado.size());
	}
	
	@Test
	void findAll_ParamsNotNull_ReturnTwoOrderedDESC() {
		Long generoId = 1L;
		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.setId(generoId);
		
		peliculaEntity1.setId(1L);
		peliculaEntity1.getGenerosDeLaPelicula().add(generoEntity);
		peliculaDTOGET1.setId(1L);
		peliculaEntity2.setId(2L);
		peliculaEntity2.getGenerosDeLaPelicula().add(generoEntity);
		peliculaDTOGET2.setId(2L);
		
		String nombre = "nombre";
		peliculaEntity1.setTitulo(nombre);
		peliculaDTOGET1.setTitulo(nombre);
		peliculaEntity2.setTitulo(nombre);
		peliculaDTOGET2.setTitulo(nombre);
		
		peliculaEntities.add(peliculaEntity1);
		peliculaEntities.add(peliculaEntity2);
		
		when(peliculaRepository.findAll()).thenReturn(peliculaEntities);
		
		when(peliculaMapper.entityToDtoGet(peliculaEntity1)).thenReturn(peliculaDTOGET1);
		when(peliculaMapper.entityToDtoGet(peliculaEntity2)).thenReturn(peliculaDTOGET2);
		
		List<PeliculaDTOGET> resultado = peliculaService.findAll(nombre, generoId, Orden.DESC);
		
		assertFalse(resultado.isEmpty());
		assertEquals(2, resultado.size());
		assertEquals(1L, resultado.get(0).getId());
		assertEquals(2L, resultado.get(1).getId());
	}
	
	@Test
	void update_WithIdNull_throwIdNullException() {
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		
		assertThrows(IdNullException.class, () -> peliculaService.update(peliculaDTOPATCH));
	}
	
	@Test
	void update_WithNotExistingId_throwEntityNotFoundException() {
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		Long notExistingId = 1L;
		peliculaDTOPATCH.setId(notExistingId);
		
		when(peliculaRepository.findById(notExistingId)).thenReturn(Optional.empty());
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.update(peliculaDTOPATCH));
	}
	
	@Test
	void update_WithExistingId_ReturnPeliculaDTO() {
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		Long existingId = 1L;
		peliculaDTOPATCH.setId(existingId);
		
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(existingId);
		
		when(peliculaRepository.findById(existingId)).thenReturn(Optional.of(peliculaEntity));
		
		when(peliculaMapper.dtoPatchToEntity(peliculaDTOPATCH, peliculaEntity)).thenReturn(peliculaEntity);
		
		when(peliculaRepository.save(peliculaEntity)).thenReturn(peliculaEntity);
		
		PeliculaDTO peliculaDTO = new PeliculaDTO();
		peliculaDTO.setId(existingId);
		when(peliculaMapper.entityToDTO(peliculaEntity)).thenReturn(peliculaDTO);
		
		PeliculaDTO resultado = peliculaService.update(peliculaDTOPATCH);
		assertNotNull(resultado);
	}
	
	@Test
	void deleteById_NotExistingId_ThrowDeleteNotExistedException() {
		Long notExistingId = 1L;
		when(peliculaRepository.findById(notExistingId)).thenReturn(Optional.empty());
		
		assertThrows(DeleteNotExistedException.class, ()-> peliculaService.deleteById(notExistingId));
	}
	
	@Test
	void deleteById_ExistingId_DoesNotThrowException() {
		Long existingId = 1L;
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		when(peliculaRepository.findById(existingId)).thenReturn(Optional.of(peliculaEntity));
		
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(existingId);
		peliculaEntity.addPersonaje(personajeEntity);
		
		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.setId(existingId);
		peliculaEntity.addGenero(generoEntity);
		
		when(peliculaRepository.save(peliculaEntity)).thenReturn(peliculaEntity);

		doNothing().when(peliculaRepository).deleteById(existingId);;
		
		assertDoesNotThrow(() -> peliculaService.deleteById(existingId));
	}
	
	@Test
	void deleteAll_VoidTable_ThrowDeleteVoidTableException() {
		when(peliculaRepository.count()).thenReturn(0L);
		
		assertThrows(DeleteVoidTableException.class, () -> peliculaService.deleteAll());
	}
	
	@Test
	void deleteAll_NotVoidTable_DoesNotThrowExceptions() {
		when(peliculaRepository.count()).thenReturn(1L);
		
		Long id = 1L;
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(id);
		
		List<PeliculaEntity> peliculaEntities = new ArrayList<>();
		peliculaEntities.add(peliculaEntity);
		
		when(peliculaRepository.findAll()).thenReturn(peliculaEntities);
		
		when(peliculaRepository.findById(id)).thenReturn(Optional.of(peliculaEntity));
		
		doNothing().when(peliculaRepository).deleteById(id);
		
		assertDoesNotThrow(() -> peliculaService.deleteAll());
	}
	
	@Test
	void asociarPersonajeApelicula_NotExistingPeliculaId_ThrowEntityNotFoundException() {
		Long notExistingPeliculaId = 1L;
		Long personajeId = 1L;
		
		when(peliculaRepository.findById(notExistingPeliculaId)).thenReturn(Optional.empty());
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.asociarPersonajeAPelicula(notExistingPeliculaId, personajeId));
	}
	
	@Test
	void asociarPersonajeApelicula_NotExistingPersonajeId_ThrowEntityNotFoundException() {
		Long notExistingPersonajeId = 1L;
		Long peliculaId = 1L;
		
		when(peliculaRepository.findById(peliculaId)).thenReturn(Optional.of(new PeliculaEntity()));
		when(personajeRepository.findById(notExistingPersonajeId)).thenReturn(Optional.empty());
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.asociarPersonajeAPelicula(peliculaId, notExistingPersonajeId));
	}
	
	@Test
	void asociarPersonajeAPelicula_ExistingIds_ReturnPeliculaDTO() {
		Long peliculaId = 1L;
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(peliculaId);
		when(peliculaRepository.findById(peliculaId)).thenReturn(Optional.of(peliculaEntity));
		
		Long personajeId = 1L;
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(personajeId);
		when(personajeRepository.findById(personajeId)).thenReturn(Optional.of(personajeEntity));
		
		when(peliculaRepository.save(peliculaEntity)).thenReturn(peliculaEntity);
		
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		personajeDTOPATCH.setId(personajeId);
		
		PeliculaDTO peliculaDTO = new PeliculaDTO();
		peliculaDTO.setId(peliculaId);
		peliculaDTO.getPersonajesEnPelicula().add(personajeDTOPATCH);
		
		when(peliculaMapper.entityToDTO(peliculaEntity)).thenReturn(peliculaDTO);
		
		PeliculaDTO resultado = peliculaService.asociarPersonajeAPelicula(peliculaId, personajeId);
		
		assertNotNull(resultado);
	}
	
	@Test
	void desasociarPersonajeApelicula_NotExistingPeliculaId_ThrowEntityNotFoundException() {
		Long notExistingPeliculaId = 1L;
		Long personajeId = 1L;
		
		when(peliculaRepository.findById(notExistingPeliculaId)).thenReturn(Optional.empty());
		
		assertThrows(EntityNotFoundException.class, () -> peliculaService.asociarPersonajeAPelicula(notExistingPeliculaId, personajeId));
	}
	
	@Test
	void desasociarPersonajeApelicula_NotExistingPersonajeId_ThrowAssociationNotExistingException() {
		Long notExistingPersonajeId = 1L;
		Long peliculaId = 1L;
		
		when(peliculaRepository.findById(peliculaId)).thenReturn(Optional.of(new PeliculaEntity()));
		
		assertThrows(AssociationNotExistingException.class, () -> peliculaService.desasociarPersonajeAPelicula(peliculaId, notExistingPersonajeId));
	}
	
	@Test
	void desasociarPersonajeAPelicula_ExistingIds_DoesNotThrowException() {
		Long personajeId = 1L;
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(personajeId);
		
		Long peliculaId = 1L;
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(peliculaId);
		
		personajeEntity.getPeliculas().add(peliculaEntity);
		peliculaEntity.getPersonajesEnPelicula().add(personajeEntity);
		
		when(peliculaRepository.findById(peliculaId)).thenReturn(Optional.of(peliculaEntity));
		
		when(peliculaRepository.save(peliculaEntity)).thenReturn(peliculaEntity);
		
		assertDoesNotThrow(()-> peliculaService.desasociarPersonajeAPelicula(peliculaId, personajeId));
	}
}
