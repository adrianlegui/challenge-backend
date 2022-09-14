package com.github.adrianlegui.challengebackendspring.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOId;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteNotExistedException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteVoidTableException;
import com.github.adrianlegui.challengebackendspring.exceptions.EntityNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.IdNullException;
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@SpringBootTest(classes = { MappersConfig.class })
@Transactional
class SerieServiceIntegrationTest {
	@Autowired
	SerieService serieService;

	@Autowired
	PersonajeRepository personajeRepository;

	@Autowired
	GeneroRepository generoRepository;

	@Autowired
	SerieRepository serieRepository;

	@Test
	void create_WithNotExistedPersonajeId_ThrowEntityNotFoundException() {
		PersonajeEntity notExistedPersonaje = personajeRepository
			.save(new PersonajeEntity());
		Long notExistedPersonajeId = notExistedPersonaje.getId();
		personajeRepository.delete(notExistedPersonaje);

		PersonajeDTOId personajeDTOId = new PersonajeDTOId();
		personajeDTOId.setId(notExistedPersonajeId);

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		serieDTOPOST.getPersonajesEnSerie().add(personajeDTOId);

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.create(serieDTOPOST));
	}

	@Test
	void create_WithNotExistedGeneroId_ThrowEntityNotFoundException() {
		GeneroEntity notExistedGenero = generoRepository
			.save(new GeneroEntity());
		Long notExistedGeneroId = notExistedGenero.getId();
		generoRepository.delete(notExistedGenero);

		GeneroDTOId generoDTOId = new GeneroDTOId();
		generoDTOId.setId(notExistedGeneroId);

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		serieDTOPOST.getGenerosDeLaSerie().add(generoDTOId);

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.create(serieDTOPOST));
	}

	@Test
	void create_WithExistedGeneroAndPersonaje_ReturnSerieDTO() {
		PersonajeEntity personajeEntity = personajeRepository
			.save(new PersonajeEntity());
		GeneroEntity generoEntity = generoRepository
			.save(new GeneroEntity());

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();

		PersonajeDTOId personajeDTOId = new PersonajeDTOId();
		personajeDTOId.setId(personajeEntity.getId());
		serieDTOPOST.getPersonajesEnSerie().add(personajeDTOId);

		GeneroDTOId generoDTOId = new GeneroDTOId();
		generoDTOId.setId(generoEntity.getId());
		serieDTOPOST.getGenerosDeLaSerie().add(generoDTOId);

		SerieDTO resultado = serieService.create(serieDTOPOST);

		assertNotNull(resultado);
		assertTrue(serieRepository.existsById(resultado.getId()));
	}

	@Test
	void findById_WithNotExistedId_ThrowEntityNotFoundException() {
		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.findById(notExistedSerieId));
	}

	@Test
	void findById_WithExistedId_ReturnSerieDTOGET() {
		SerieEntity existedSerie = serieRepository
			.save(new SerieEntity());
		Long existedSerieId = existedSerie.getId();
		existedSerie.setTitulo("titulo");
		existedSerie = serieRepository.save(existedSerie);

		SerieDTOGET resultado = serieService.findById(existedSerieId);

		assertNotNull(resultado);
		assertEquals(existedSerie.getTitulo(), resultado.getTitulo());
	}

	@Test
	void findAll_ParamsNotNull_ReturnEmptyList() {
		String notExistedName = "nombre no existente";

		GeneroEntity notExistedGenero = generoRepository
			.save(new GeneroEntity());
		Long notExistedGeneroId = notExistedGenero.getId();
		generoRepository.delete(notExistedGenero);

		Orden orden = Orden.DESC;

		List<SerieDTOGET> resultado = serieService
			.findAll(notExistedName, notExistedGeneroId, orden);

		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());
	}

	@Test
	void findAll_ParamsNull_ReturnAll() {
		serieRepository.save(new SerieEntity());

		List<SerieDTOGET> resultado = serieService
			.findAll(null, null, null);

		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
		assertEquals(serieRepository.count(), resultado.size());
	}

	@Test
	void findAll_ParamsNotNull_ReturnOne() {
		Orden orden = Orden.DESC;

		GeneroEntity existedGeneroEntity = generoRepository
			.save(new GeneroEntity());
		Long existedGeneroId = existedGeneroEntity.getId();

		String existedName = "titulo existente";

		SerieEntity serieEntity = serieRepository
			.save(new SerieEntity());
		serieEntity.setTitulo(existedName);
		serieEntity.addGenero(existedGeneroEntity);
		serieEntity = serieRepository.save(serieEntity);

		List<SerieDTOGET> resultado = serieService
			.findAll(existedName, existedGeneroId, orden);

		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
		assertEquals(1, resultado.size());
		assertEquals(
			serieEntity.getTitulo(),
			resultado.get(0).getTitulo());
	}

	@Test
	void findAll_ParamsNotNull_ReturnTwoOrderedDESC() {
		serieRepository.save(new SerieEntity());

		GeneroEntity existedGenero = generoRepository
			.save(new GeneroEntity());
		Long existedGeneroId = existedGenero.getId();

		String existedName = "titulo99";

		SerieEntity serieEntity2 = serieRepository
			.save(new SerieEntity());
		serieEntity2.setTitulo(existedName);
		serieEntity2.addGenero(existedGenero);
		serieEntity2.setFechaDeCreacion(LocalDate.now());
		serieEntity2 = serieRepository.save(serieEntity2);

		SerieEntity serieEntity3 = serieRepository
			.save(new SerieEntity());
		serieEntity3.setTitulo(existedName);
		serieEntity3.addGenero(existedGenero);
		serieEntity3.setFechaDeCreacion(LocalDate.now().plusDays(1L));
		serieEntity3 = serieRepository.save(serieEntity3);

		Orden orden = Orden.DESC;

		List<SerieDTOGET> resultado = serieService
			.findAll(existedName, existedGeneroId, orden);

		assertNotNull(resultado);
		assertFalse(resultado.isEmpty());
		assertEquals(2, resultado.size());
		assertEquals(serieEntity3.getId(), resultado.get(0).getId());
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
		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);

		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();
		serieDTOPATCH.setId(notExistedSerieId);

		assertThrows(
			EntityNotFoundException.class,
			() -> serieService.update(serieDTOPATCH));
	}

	@Test
	void update_WithExistedId_ReturnSerieDTO() {
		SerieEntity existedSerie = serieRepository
			.save(new SerieEntity());
		Long existedSerieId = existedSerie.getId();
		existedSerie.setTitulo("titulo antiguo");
		existedSerie = serieRepository.save(existedSerie);

		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();
		serieDTOPATCH.setId(existedSerieId);
		serieDTOPATCH.setTitulo("titulo nuevo");

		SerieDTO resultado = serieService.update(serieDTOPATCH);

		Optional<SerieEntity> serieOptional = serieRepository
			.findById(existedSerieId);

		assertNotNull(resultado);
		assertEquals(serieDTOPATCH.getTitulo(), resultado.getTitulo());
		assertEquals(
			serieDTOPATCH.getTitulo(),
			serieOptional.get().getTitulo());
	}

	@Test
	void deleteById_WithNotExistedId_ThrowDeleteNotExistedException() {
		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.deleteById(notExistedSerieId);

		assertThrows(
			DeleteNotExistedException.class,
			() -> serieService.deleteById(notExistedSerieId));
	}

	@Test
	void deleteById_WithExistedId_DoesNotThrowException() {
		SerieEntity existedSerie = serieRepository
			.save(new SerieEntity());
		Long existedSerieId = existedSerie.getId();

		assertDoesNotThrow(
			() -> serieService.deleteById(existedSerieId));

		assertFalse(serieRepository.existsById(existedSerieId));
	}

	@Test
	void deleteAll_VoidTable_ThrowDeleteVoidTableException() {
		if (0 >= serieRepository.count()) {
			for (SerieEntity serie : serieRepository.findAll()) {
				serie.removeAllGenero();
				serie.removeAllPersonaje();
				serieRepository.save(serie);
				serieRepository.delete(serie);
			}
		}

		assertThrows(
			DeleteVoidTableException.class,
			() -> serieService.deleteAll());
	}

	@Test
	void deleteAll_NotVoidTable_DoesNotThrowException() {
		serieRepository.save(new SerieEntity());

		assertDoesNotThrow(() -> serieRepository.deleteAll());
		assertEquals(0L, serieRepository.count());
	}

}
