package com.github.adrianlegui.challengebackendspring.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTO;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPOST;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOId;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
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

@ExtendWith(MockitoExtension.class)
class PersonajeServiceTest {
	@Mock
	PersonajeRepository personajeRepository;
	@Mock
	PeliculaRepository peliculaRepository;
	@Mock
	SerieRepository serieRepository;
	@Mock
	PersonajeMapper personajeMapper;
	
	Long id = 1L;
	Optional<PersonajeEntity> personajeOptional;
	
	@InjectMocks
	PersonajeService personajeService = new PersonajeService();

	// test findAll
	List<PersonajeDTOGET> resultadoFindAll;
	List<PersonajeEntity> personajeEntities;
	
	PersonajeEntity personajeConNombre;
	Long idConNombre = 1L;
	String nombre = "nombre";
	
	PersonajeDTOGET personajeDTOGETConNombre;
	
	PersonajeEntity personajeConEdad;
	Long idConEdad = 2L;	
	Integer edad = 18;

	PersonajeDTOGET personajeDTOGETConEdad;
	
	PersonajeEntity personajeConPelicula;
	Long idConPelicula = 3L;
	PeliculaEntity peliculaEntity;
	Long idPelicula = 1L;
	
	PersonajeDTOGET personajeDTOGETConPelicula;

	PersonajeEntity personajeConSerie;
	Long idConSerie = 4L;
	SerieEntity serieEntity;
	Long idSerie = 1L;
	
	PersonajeDTOGET personajeDTOGETConSerie;
	// end test findAll
	
	// test update
	Long idUpdate = 1L;
	PersonajeDTOPATCH personajeDTOPATCH;
	// end test update
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		// test findAll
		personajeEntities = new ArrayList<>();
		
		// inicializar entities
		personajeConNombre = new PersonajeEntity();
		personajeConNombre.setId(idConNombre);
		personajeConNombre.setNombre(nombre);
		personajeEntities.add(personajeConNombre);
		
		personajeConEdad = new PersonajeEntity();
		personajeConEdad.setId(idConEdad);
		personajeConEdad.setEdad(edad);
		personajeEntities.add(personajeConEdad);
		
		personajeConPelicula = new PersonajeEntity();
		personajeConPelicula.setId(idConPelicula);
		peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(idPelicula);
		personajeConPelicula.getPeliculas().add(peliculaEntity);
		personajeEntities.add(personajeConPelicula);
		
		personajeConSerie = new PersonajeEntity();
		personajeConSerie.setId(idConSerie);
		serieEntity = new SerieEntity();
		serieEntity.setId(idSerie);
		personajeConSerie.getSeries().add(serieEntity);
		personajeEntities.add(personajeConSerie);
		
		// inicializar DTO
		personajeDTOGETConNombre= new PersonajeDTOGET();
		personajeDTOGETConNombre.setId(idConNombre);
		personajeDTOGETConNombre.setNombre(nombre);
		
		personajeDTOGETConEdad = new PersonajeDTOGET();
		personajeDTOGETConEdad.setId(idConEdad);
		
		personajeDTOGETConPelicula = new PersonajeDTOGET();
		personajeDTOGETConPelicula.setId(idConPelicula);
		
		personajeDTOGETConSerie = new PersonajeDTOGET();
		personajeDTOGETConSerie.setId(idConSerie);
		// end test findAll
		
		personajeDTOPATCH = new PersonajeDTOPATCH();
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void create_PersonajeDTOPOST_ReturnPersonajeDTO() {
		// Creación de personaje
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		
		Long idPelicula = 1L;
		PeliculaDTOId peliculaDTOId = new PeliculaDTOId();
		peliculaDTOId.setId(idPelicula);
		personajeDTOPOST.getPeliculas().add(peliculaDTOId);
		
		Long idSerie = 1L;
		SerieDTOId serieDTOId = new SerieDTOId();
		serieDTOId.setId(idSerie);
		personajeDTOPOST.getSeries().add(serieDTOId);
		
		
		when(peliculaRepository.existsById(anyLong())).thenReturn(true);
		when(serieRepository.existsById(anyLong())).thenReturn(true);
		
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		when(personajeMapper.dtoPostToDtoPatch(personajeDTOPOST)).thenReturn(personajeDTOPATCH);
		
		PersonajeEntity personajeEntityParaCrear = new PersonajeEntity();
		when(personajeMapper.dtoPatchToEntity(personajeDTOPATCH)).thenReturn(personajeEntityParaCrear);
		
		Long idDto = 1L;
		PersonajeEntity personajeEntityCreado = new PersonajeEntity();
		personajeEntityCreado.setId(idDto);
		when(personajeRepository.save(personajeEntityParaCrear)).thenReturn(personajeEntityCreado);
		
		// fin de creación de personaje

		// asociar películas
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(idPelicula);
		
		when(peliculaRepository.findById(anyLong())).thenReturn(Optional.of(peliculaEntity));
		when(peliculaRepository.save(peliculaEntity)).thenReturn(peliculaEntity);
		// fin de asociar películas
		
		// asociar series
		SerieEntity serieEntity = new SerieEntity();
		serieEntity.setId(idSerie);
		
		when(serieRepository.findById(anyLong())).thenReturn(Optional.of(serieEntity));
		when(serieRepository.save(serieEntity)).thenReturn(serieEntity);
		// fin de asociar series
		
		when(personajeRepository.findById(anyLong())).thenReturn(Optional.of(personajeEntityCreado));
		
		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();
		serieDTOPATCH.setId(idSerie);
		
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		peliculaDTOPATCH.setId(idPelicula);
		
		PersonajeDTO personajeDTO = new PersonajeDTO();
		personajeDTO.setId(idDto);
		personajeDTO.getSeries().add(serieDTOPATCH);
		personajeDTO.getPeliculas().add(peliculaDTOPATCH);
		
		when(personajeMapper.entityToDto(personajeEntityCreado)).thenReturn(personajeDTO);
		
		PersonajeDTO resultado = personajeService.create(personajeDTOPOST);
		
		assertNotNull(resultado);
	}
	
	@Test
	void create_PersonajeDTOPOSTWithPeliculaIdNotExist_ThrowEntityNotFoundException() {
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		
		Long idNotFound = 1L;
		PeliculaDTOId peliculaIdNotFound = new PeliculaDTOId();
		peliculaIdNotFound.setId(idNotFound);
		personajeDTOPOST.getPeliculas().add(peliculaIdNotFound);
		
		when(peliculaRepository.existsById(idNotFound)).thenReturn(false);
		
		assertThrows(EntityNotFoundException.class, () -> personajeService.create(personajeDTOPOST));
	}
	
	@Test
	void create_PersonajeDTOPOSTWithSerieIdNotExist_ThrowEntityNotFoundException() {
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		
		Long idNotFound = 1L;
		SerieDTOId serieIdNotFound = new SerieDTOId();
		serieIdNotFound.setId(idNotFound);
		personajeDTOPOST.getSeries().add(serieIdNotFound);
		
		when(serieRepository.existsById(idNotFound)).thenReturn(false);
		
		assertThrows(EntityNotFoundException.class, () -> personajeService.create(personajeDTOPOST));
	}
	
	@Test
	void findByid_IdNotExist_ThrowEntityNotFoundException() {
		personajeOptional = Optional.empty();
		
		when(personajeRepository.findById(id)).thenReturn(personajeOptional);
		
		assertThrows(EntityNotFoundException.class, () -> personajeService.findById(id));
	}
	
	@Test
	void findById_IdExist_ReturnPersonajeDTOGET() {
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(id);
		
		PersonajeDTOGET personajeDTOGET;
		personajeDTOGET = new PersonajeDTOGET();
		personajeDTOGET.setId(id);
		
		personajeOptional = Optional.of(personajeEntity);
		
		when(personajeRepository.findById(id)).thenReturn(personajeOptional);
		when(personajeMapper.entityToDtoGet(personajeEntity)).thenReturn(personajeDTOGET);
		
		PersonajeDTOGET resultado = personajeService.findById(id);
		
		assertNotNull(resultado);
		
	}
	
	@Test
	void findAll_RequestParamsNotNull_ReturnEmpty() {
		when(personajeRepository.findAll()).thenReturn(personajeEntities);
		
		List<PersonajeDTOGET> resultado = personajeService.findAll(nombre, edad, idPelicula, idSerie);
		
		assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findAll_RequestParamsNull_ReturnAll() {
		when(personajeRepository.findAll()).thenReturn(personajeEntities);
		
		when(personajeMapper.entityToDtoGet(personajeConNombre)).thenReturn(personajeDTOGETConNombre);
		when(personajeMapper.entityToDtoGet(personajeConEdad)).thenReturn(personajeDTOGETConEdad);
		when(personajeMapper.entityToDtoGet(personajeConPelicula)).thenReturn(personajeDTOGETConPelicula);
		when(personajeMapper.entityToDtoGet(personajeConSerie)).thenReturn(personajeDTOGETConSerie);
		
		resultadoFindAll = personajeService.findAll(null, null, null, null);
		
		assertFalse(resultadoFindAll.isEmpty());
	}
	
	@Test
	void findAll_RequestParamName_ReturnPersonajeConNombre() {
		when(personajeRepository.findAll()).thenReturn(personajeEntities);
		
		when(personajeMapper.entityToDtoGet(personajeConNombre)).thenReturn(personajeDTOGETConNombre);
		
		resultadoFindAll = personajeService.findAll(nombre, null, null, null);
		
		assertFalse(resultadoFindAll.isEmpty());
		assertEquals(idConNombre, resultadoFindAll.get(0).getId());
	}
	
	@Test
	void findAll_RequestParamAge_ReturnPersonajeConEdad() {
		when(personajeRepository.findAll()).thenReturn(personajeEntities);
		
		when(personajeMapper.entityToDtoGet(personajeConEdad)).thenReturn(personajeDTOGETConEdad);
		
		resultadoFindAll = personajeService.findAll(null, edad, null, null);
		
		assertFalse(resultadoFindAll.isEmpty());
		assertEquals(idConEdad, resultadoFindAll.get(0).getId());
	}
	
	@Test
	void findAll_RequestParamIdPelicula_ReturnPersonajeConPelicula() {
		when(personajeRepository.findAll()).thenReturn(personajeEntities);
		
		when(personajeMapper.entityToDtoGet(personajeConPelicula)).thenReturn(personajeDTOGETConPelicula);
		
		resultadoFindAll = personajeService.findAll(null, null, idPelicula, null);
		
		assertFalse(resultadoFindAll.isEmpty());
		assertEquals(idConPelicula, resultadoFindAll.get(0).getId());
	}
	
	@Test
	void findAll_RequestParamIdSerie_ReturnPersonajeConSerie() {
		when(personajeRepository.findAll()).thenReturn(personajeEntities);
		
		when(personajeMapper.entityToDtoGet(personajeConSerie)).thenReturn(personajeDTOGETConSerie);
		
		resultadoFindAll = personajeService.findAll(null, null, null, idSerie);
		
		assertFalse(resultadoFindAll.isEmpty());
		assertEquals(idConSerie, resultadoFindAll.get(0).getId());
	}
	
	@Test
	void update_WithIdNull_ThrowIdNullException() {
		assertThrows(IdNullException.class, () -> personajeService.update(personajeDTOPATCH));
	}
	
	@Test
	void update_WithNotExistingId_ThrowPersonajeEntityNotFoundException() {
		personajeDTOPATCH.setId(idUpdate);

		when(personajeRepository.findById(idUpdate)).thenReturn(Optional.empty());
		
		assertThrows(EntityNotFoundException.class, () -> personajeService.update(personajeDTOPATCH));
	}
	
	@Test
	void update_WithExistingId_ReturnPersonajeDTO() {
		personajeDTOPATCH.setId(idUpdate);

		PersonajeDTO personajeDTO = new PersonajeDTO();
		personajeDTO.setId(idUpdate);
		
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(idUpdate);
		
		when(personajeRepository.findById(idUpdate)).thenReturn(Optional.of(personajeEntity));
		when(personajeMapper.dtoPatchToEntity(personajeDTOPATCH, personajeEntity)).thenReturn(personajeEntity);
		when(personajeRepository.save(personajeEntity)).thenReturn(personajeEntity);
		when(personajeMapper.entityToDto(personajeEntity)).thenReturn(personajeDTO);
		
		PersonajeDTO resultado = personajeService.update(personajeDTOPATCH);
		
		assertNotNull(resultado);
	}
	
	@Test
	void deleteById_NotExistingId_ThrowDeleteNotExistedException() {
		Long idDelete = 1L;
		
		when(personajeRepository.findById(idDelete)).thenReturn(Optional.empty());
		
		assertThrows(DeleteNotExistedException.class, () -> personajeService.deleteById(idDelete));
	}
	
	@Test
	void deleteById_ExistingId_DoesNotThrowExceptions() {
		Long idDelete = 1L;
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(idDelete);
		
		when(personajeRepository.findById(idDelete)).thenReturn(Optional.of(personajeEntity));
		
		Long idPelicula = 1L;
		PeliculaEntity peliculaEntity = new PeliculaEntity();
		peliculaEntity.setId(idPelicula);
		peliculaEntity.addPersonaje(personajeEntity);
		
		when(peliculaRepository.save(peliculaEntity)).thenReturn(peliculaEntity);
		
		Long idSerie = 1L;
		SerieEntity serieEntity = new SerieEntity();
		serieEntity.setId(idSerie);
		serieEntity.addPersonaje(personajeEntity);
		
		when(serieRepository.save(serieEntity)).thenReturn(serieEntity);
		
		assertDoesNotThrow(() -> personajeService.deleteById(idDelete));
	}
	
	@Test
	void deleteAll_VoidTable_ThrowDeleteVoidTableException() {
		when(personajeRepository.count()).thenReturn(0L);
		
		assertThrows(DeleteVoidTableException.class, () -> personajeService.deleteAll());
	}
	
	@Test
	void deleteAll_NotVoidTable_DoesNotThrowExceptions() {
		when(personajeRepository.count()).thenReturn(1L);
		
		Long id = 1L;
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeEntity.setId(id);
		
		List<PersonajeEntity> personajeEntities = new ArrayList<>();
		personajeEntities.add(personajeEntity);
		when(personajeRepository.findAll()).thenReturn(personajeEntities);
		
		when(personajeRepository.findById(id)).thenReturn(Optional.of(personajeEntity));
		
		doNothing().when(personajeRepository).deleteById(id);
		
		assertDoesNotThrow(() -> personajeService.deleteAll());
	}
}
