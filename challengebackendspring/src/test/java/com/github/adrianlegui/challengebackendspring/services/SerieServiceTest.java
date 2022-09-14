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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOId;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.AssociationNotExistingException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteNotExistedException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteVoidTableException;
import com.github.adrianlegui.challengebackendspring.exceptions.EntityNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.IdNullException;
import com.github.adrianlegui.challengebackendspring.mappers.SerieMapper;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@ExtendWith(MockitoExtension.class)
class SerieServiceTest {
	@InjectMocks
	SerieService serieService;

	@Mock
	GeneroRepository generoRepository;

	@Mock
	PersonajeRepository personajeRepository;
	@Mock
	SerieRepository serieRepository;

	@Mock
	SerieMapper serieMapper;

	@Test
	void create_WithNotExistedPersonajeId_ThrowEntityNotFoundException() {
		Long notExistedPersonajeId = 1L;
		PersonajeDTOId notExistedPersonaje = new PersonajeDTOId();
		notExistedPersonaje.setId(notExistedPersonajeId);

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		serieDTOPOST.getPersonajesEnSerie()
			.add(notExistedPersonaje);

		when(
			personajeRepository.existsById(notExistedPersonajeId))
			.thenReturn(false);

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.create(serieDTOPOST));
	}

	@Test
	void create_WithNotExistedGeneroId_ThrowEntityNotFoundException() {
		Long notExistedGeneroId = 1L;
		GeneroDTOId notExistedGenero = new GeneroDTOId();
		notExistedGenero.setId(notExistedGeneroId);

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		serieDTOPOST.getGenerosDeLaSerie().add(notExistedGenero);

		when(generoRepository.existsById(notExistedGeneroId))
			.thenReturn(false);

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.create(serieDTOPOST));
	}

	@Test
	void create_WithExistedGeneroAndPersonaje_ReturnSerieDTO() {
		Long existedGeneroId = 1L;
		GeneroDTOId existedGeneroDTOId = new GeneroDTOId();
		existedGeneroDTOId.setId(existedGeneroId);

		Long existedPersonajeId = 1L;
		PersonajeDTOId existedPersonajeDTOId = new PersonajeDTOId();
		existedPersonajeDTOId.setId(existedPersonajeId);

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		serieDTOPOST.getGenerosDeLaSerie()
			.add(existedGeneroDTOId);
		serieDTOPOST.getPersonajesEnSerie()
			.add(existedPersonajeDTOId);

		// check if the characters exist
		when(personajeRepository.existsById(existedPersonajeId))
			.thenReturn(true);
		// check if the genres exist
		when(generoRepository.existsById(existedGeneroId))
			.thenReturn(true);
		// create serie without associations
		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();

		when(serieMapper.dtoPostToDtoPatch(serieDTOPOST))
			.thenReturn(serieDTOPATCH);

		SerieEntity serieEntity = new SerieEntity();

		when(serieMapper.dtoPatchToEntity(serieDTOPATCH))
			.thenReturn(serieEntity);

		when(serieRepository.save(serieEntity))
			.thenReturn(serieEntity);
		// create association to characters
		PersonajeEntity personajeEntity = new PersonajeEntity();

		when(personajeRepository.findById(existedPersonajeId))
			.thenReturn(Optional.of(personajeEntity));

		when(serieRepository.save(serieEntity))
			.thenReturn(serieEntity);
		// create association to genres
		GeneroEntity generoEntity = new GeneroEntity();

		when(generoRepository.findById(existedGeneroId))
			.thenReturn(Optional.of(generoEntity));

		when(serieRepository.save(serieEntity))
			.thenReturn(serieEntity);

		when(serieMapper.entityToDTO(serieEntity))
			.thenReturn(new SerieDTO());

		SerieDTO resultado = serieService.create(serieDTOPOST);

		assertNotNull(resultado);
	}

	@Test
	void findById_WithNotExistedId_ThrowEntityNotFoundException() {
		Long notExistedId = 1L;

		when(serieRepository.findById(notExistedId))
			.thenReturn(Optional.empty());

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.findById(notExistedId));
	}

	@Test
	void findById_WithExistedId_ReturnSerieDTOGET() {
		Long existedId = 1L;

		SerieEntity serieEntity = new SerieEntity();

		when(serieRepository.findById(existedId))
			.thenReturn(Optional.of(serieEntity));

		SerieDTOGET serieDTOGET = new SerieDTOGET();
		when(serieMapper.entityToDtoGet(serieEntity))
			.thenReturn(serieDTOGET);

		SerieDTOGET resultado = serieService.findById(existedId);

		assertNotNull(resultado);
	}

	@Test
	void findAll_ParamsNotNull_ReturnEmptyList() {
		String nombre = "nombre";
		Long generoId = 1L;
		Orden orden = Orden.ASC;

		List<SerieEntity> serieEntities = new ArrayList<>();

		when(serieRepository.findAll()).thenReturn(serieEntities);

		List<SerieDTOGET> resultado = serieService
			.findAll(nombre, generoId, orden);

		assertTrue(resultado.isEmpty());
	}

	@Test
	void findAll_ParamsNull_ReturnAll() {
		List<SerieEntity> serieEntities = new ArrayList<>();
		SerieEntity serieEntity = new SerieEntity();
		serieEntities.add(serieEntity);

		when(serieRepository.findAll()).thenReturn(serieEntities);

		when(serieMapper.entityToDtoGet(serieEntity))
			.thenReturn(new SerieDTOGET());

		List<SerieDTOGET> resultado = serieService
			.findAll(null, null, null);

		assertFalse(resultado.isEmpty());
		assertEquals(1, resultado.size());
	}

	@Test
	void findAll_ParamsNotNull_ReturnOne() {
		String nombre = "nombre";
		Long generoId = 1L;
		Orden orden = Orden.ASC;

		SerieEntity serieEntity1 = new SerieEntity();
		serieEntity1.setTitulo(nombre);
		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.setId(generoId);
		serieEntity1.addGenero(generoEntity);

		SerieEntity serieEntity2 = new SerieEntity();

		List<SerieEntity> serieEntities = new ArrayList<>();
		serieEntities.add(serieEntity1);
		serieEntities.add(serieEntity2);

		when(serieRepository.findAll()).thenReturn(serieEntities);

		when(serieMapper.entityToDtoGet(serieEntity1))
			.thenReturn(new SerieDTOGET());

		List<SerieDTOGET> resultado = serieService
			.findAll(nombre, generoId, orden);

		assertFalse(resultado.isEmpty());
		assertEquals(1, resultado.size());
	}

	@Test
	void findAll_ParamsNotNull_ReturnTwoOrderedDESC() {
		String nombre = "nombre";
		Long generoId = 1L;
		Orden orden = Orden.DESC;
		Long serieId = 1L;

		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.setId(generoId);

		SerieEntity serieEntity1 = new SerieEntity();
		serieEntity1.addGenero(generoEntity);
		serieEntity1.setTitulo(nombre);
		serieEntity1.setFechaDeCreacion(LocalDate.now());

		SerieDTOGET serieDTOGET1 = new SerieDTOGET();
		serieDTOGET1.setTitulo(nombre);
		serieDTOGET1.setFechaDeCreacion(LocalDate.now());

		SerieEntity serieEntity2 = new SerieEntity();
		serieEntity2.setId(serieId);
		serieEntity2.setTitulo(nombre);
		serieEntity2.addGenero(generoEntity);
		serieEntity2
			.setFechaDeCreacion(LocalDate.now().plusDays(1L));

		SerieDTOGET serieDTOGET2 = new SerieDTOGET();
		serieDTOGET2.setId(serieId);
		serieDTOGET2.setTitulo(nombre);
		serieDTOGET2
			.setFechaDeCreacion(LocalDate.now().plusDays(1L));

		SerieEntity serieEntity3 = new SerieEntity();

		List<SerieEntity> serieEntities = new ArrayList<>();
		serieEntities.add(serieEntity1);
		serieEntities.add(serieEntity2);
		serieEntities.add(serieEntity3);

		when(serieRepository.findAll()).thenReturn(serieEntities);

		when(serieMapper.entityToDtoGet(serieEntity1))
			.thenReturn(serieDTOGET1);
		when(serieMapper.entityToDtoGet(serieEntity2))
			.thenReturn(serieDTOGET2);

		List<SerieDTOGET> resultado = serieService
			.findAll(nombre, generoId, orden);

		assertFalse(resultado.isEmpty());
		assertEquals(2, resultado.size());
		assertEquals(serieId, resultado.get(0).getId());
	}

	@Test
	void update_WithIdNull_ThrowIdNullException() {
		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();

		assertThrows(
			IdNullException.class,
			() -> serieService.update(serieDTOPATCH));
	}

	@Test
	void update_WithNotExistedId_ThrowEntityNotFoundException() {
		Long notExistedId = 1L;
		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();
		serieDTOPATCH.setId(notExistedId);

		when(serieRepository.findById(notExistedId))
			.thenReturn(Optional.empty());

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.update(serieDTOPATCH));
	}

	@Test
	void update_WithExistedId_ReturnSerieDTO() {
		Long existedId = 1L;
		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();
		serieDTOPATCH.setId(existedId);

		SerieEntity serieEntity = new SerieEntity();

		when(serieRepository.findById(existedId))
			.thenReturn(Optional.of(serieEntity));

		when(
			serieMapper
				.dtoPatchToEntity(serieDTOPATCH, serieEntity))
			.thenReturn(serieEntity);

		when(serieRepository.save(serieEntity))
			.thenReturn(serieEntity);

		SerieDTO serieDTO = new SerieDTO();
		when(serieMapper.entityToDTO(serieEntity))
			.thenReturn(serieDTO);

		SerieDTO resultado = serieService.update(serieDTOPATCH);

		assertNotNull(resultado);
	}

	@Test
	void deleteById_WithNotExistedId_ThrowDeleteNotExistedException() {
		Long notExistedId = 1L;

		when(serieRepository.findById(notExistedId))
			.thenReturn(Optional.empty());

		assertThrows(
			DeleteNotExistedException.class,
			() -> serieService.deleteById(notExistedId));
	}

	@Test
	void deleteById_WithExistedId_DoesNotThrowException() {
		Long existedId = 1L;
		SerieEntity serieEntity = new SerieEntity();
		serieEntity.setId(existedId);

		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(existedId);
		serieEntity.addPersonaje(personajeEntity);

		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.setId(existedId);
		serieEntity.addGenero(generoEntity);

		when(serieRepository.findById(existedId))
			.thenReturn(Optional.of(serieEntity));

		when(serieRepository.save(serieEntity))
			.thenReturn(serieEntity);

		assertDoesNotThrow(
			() -> serieService.deleteById(existedId));
	}

	@Test
	void deleteAll_VoidTable_ThrowDeleteVoidTableException() {
		when(serieRepository.count()).thenReturn(0L);

		assertThrows(
			DeleteVoidTableException.class,
			() -> serieService.deleteAll());
	}

	@Test
	void deleteAll_NotVoidTable_DoesNotThrowException() {
		when(serieRepository.count()).thenReturn(1L);

		SerieEntity serieEntity = new SerieEntity();
		List<SerieEntity> serieEntities = new ArrayList<>();
		serieEntities.add(serieEntity);

		when(serieRepository.findAll()).thenReturn(serieEntities);

		when(serieRepository.save(serieEntity))
			.thenReturn(serieEntity);
		doNothing().when(serieRepository).delete(serieEntity);

		assertDoesNotThrow(() -> serieService.deleteAll());
	}

	@Test
	void asociarPersonajeASerie_NotExistedPersonaje_ThrowEntityNotFoundException() {
		Long notExistedPersonajeId = 1L;
		Long existedSerieId = 1L;

		when(personajeRepository.findById(notExistedPersonajeId))
			.thenReturn(Optional.of(new PersonajeEntity()));

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.asociarPersonajeASerie(
				existedSerieId,
				notExistedPersonajeId));
	}

	@Test
	void asociarPersonajeASerie_NotExistedSerie_ThrowEntityNotFoundException() {
		Long notExistedSerieId = 1L;
		Long existedPersonajeId = 1L;

		when(personajeRepository.findById(existedPersonajeId))
			.thenReturn(Optional.of(new PersonajeEntity()));

		when(serieRepository.findById(notExistedSerieId))
			.thenReturn(Optional.empty());

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.asociarPersonajeASerie(
				notExistedSerieId,
				existedPersonajeId));
	}

	@Test
	void asociarPersonajeASerie_SerieAndGeneroExisting_ReturnSerieDTO() {
		Long existedPersonajeId = 1L;
		Long existedSerieId = 1L;

		when(personajeRepository.findById(existedPersonajeId))
			.thenReturn(Optional.of(new PersonajeEntity()));

		SerieEntity serieEntity = new SerieEntity();
		when(serieRepository.findById(existedSerieId))
			.thenReturn(Optional.of(serieEntity));

		when(serieRepository.save(serieEntity))
			.thenReturn(serieEntity);

		when(serieMapper.entityToDTO(serieEntity))
			.thenReturn(new SerieDTO());

		SerieDTO resultado = serieService.asociarPersonajeASerie(
			existedSerieId,
			existedPersonajeId);

		assertNotNull(resultado);
	}

	@Test
	void desasociarPersonajeASerie_NotExistedAssociation_ThrowAssociationNotExistingException() {
		Long notExistedAssociationId = 1L;
		Long existedSerieId = 1L;

		SerieEntity serieEntity = new SerieEntity();

		when(serieRepository.findById(existedSerieId))
			.thenReturn(Optional.of(serieEntity));

		assertThrows(
			AssociationNotExistingException.class,
			() -> serieService.desasociarPersonajeASerie(
				existedSerieId,
				notExistedAssociationId));
	}

	@Test
	void desasociarPersonajeASerie_NotExistedSerie_ThrowEntityNotFoundException() {
		Long notExistedSerieId = 1L;
		Long existedPersonajeId = 1L;

		when(serieRepository.findById(notExistedSerieId))
			.thenReturn(Optional.empty());

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.desasociarPersonajeASerie(
				notExistedSerieId,
				existedPersonajeId));
	}

	@Test
	void desasociarPersonajeASerie_SerieAndAssociationExisting_DoesNotThrowException() {
		Long existedSerieId = 1L;
		SerieEntity serieEntity = new SerieEntity();
		serieEntity.setId(existedSerieId);

		Long existedPersonajeId = 1L;
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(existedPersonajeId);
		serieEntity.addPersonaje(personajeEntity);

		when(serieRepository.findById(existedSerieId))
			.thenReturn(Optional.of(serieEntity));

		when(serieRepository.save(serieEntity)).thenReturn(serieEntity);
		
		assertDoesNotThrow(
			() -> serieService.desasociarPersonajeASerie(
				existedSerieId,
				existedPersonajeId));
	}

}
