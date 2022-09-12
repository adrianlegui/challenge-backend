package com.github.adrianlegui.challengebackendspring.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@SpringBootTest(classes = {MappersConfig.class})
class PersonajeServiceIntegrationTest {
	@Autowired
	PersonajeService personajeService;
	@Autowired
	PersonajeRepository personajeRepository;
	@Autowired
	SerieRepository serieRepository;
	@Autowired
	PeliculaRepository peliculaRepository;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void create_PersonajeDTOPOST_ReturnPersonajeDTO() {
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		
		PeliculaEntity peliculaEntity = peliculaRepository.save(new PeliculaEntity());
		PeliculaDTOId peliculaDTOId = new PeliculaDTOId();
		peliculaDTOId.setId(peliculaEntity.getId());
		personajeDTOPOST.getPeliculas().add(peliculaDTOId);
		
		SerieEntity serieEntity = serieRepository.save(new SerieEntity());
		SerieDTOId serieDTOId = new SerieDTOId();
		serieDTOId.setId(serieEntity.getId());
		personajeDTOPOST.getSeries().add(serieDTOId);
		
		PersonajeDTO resultado = personajeService.create(personajeDTOPOST);
		
		assertTrue(personajeRepository.existsById(resultado.getId()));
	}
	
	@Test
	void create_PersonajeDTOPOSTWithPeliculaIdNotExist_ThrowEntityNotFoundException() {
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		
		Long idPeliculaNotFound = 1L;
		if(peliculaRepository.existsById(idPeliculaNotFound))
			peliculaRepository.deleteById(idPeliculaNotFound);
			
		PeliculaDTOId peliculaDTOId = new PeliculaDTOId();
		peliculaDTOId.setId(idPeliculaNotFound);
		
		personajeDTOPOST.getPeliculas().add(peliculaDTOId);
		
		assertThrows(EntityNotFoundException.class,() -> personajeService.create(personajeDTOPOST));
	}
	
	@Test
	void create_PersonajeDTOPOSTWithSerieIdNotExist_ThrowEntityNotFoundException() {
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		
		Long idSerieNotFound = 1L;
		if(serieRepository.existsById(idSerieNotFound))
			serieRepository.deleteById(idSerieNotFound);
		
		SerieDTOId serieDTOId = new SerieDTOId();
		serieDTOId.setId(idSerieNotFound);
		
		personajeDTOPOST.getSeries().add(serieDTOId);
		
		assertThrows(EntityNotFoundException.class,() -> personajeService.create(personajeDTOPOST));
	}
	
	@Test
	void findById_NotExistingId_ThrowPersonajeEntityNotFoundException() {
		Long idNotExist = 99L;
		
		if(personajeRepository.existsById(idNotExist))
			personajeRepository.deleteById(idNotExist);
		
		assertThrows(EntityNotFoundException.class, () -> personajeService.findById(idNotExist));
	}
	
	@Test
	void findById_ExistingId_returnPersonajeDTOGET() {
		PersonajeEntity personajeExistente = personajeRepository.save(new PersonajeEntity());
		
		PersonajeDTOGET resultado = personajeService.findById(personajeExistente.getId());
		
		assertNotNull(resultado);
	}
	

	@Test
	void deleteById_NotExistingId_ThrowDeleteNotExistedException() {
		Long idNotExist = 99L;
		
		if(personajeRepository.existsById(idNotExist))
			personajeRepository.deleteById(idNotExist);
		
		assertThrows(DeleteNotExistedException.class, () -> personajeService.deleteById(idNotExist));
	}
	
	@Test
	void deleteById_ExistingId_DoesNotThrowExceptions() {
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		PersonajeDTO personajeDTO = personajeService.create(personajeDTOPOST);

		assertDoesNotThrow(() -> personajeService.deleteById(personajeDTO.getId()));
	}
	
	@Test
	void deleteAll_VoidTable_ThrowDeleteVoidTableException() {
		if(0L < personajeRepository.count())
			personajeService.deleteAll();
		
		assertThrows(DeleteVoidTableException.class, () -> personajeService.deleteAll());
	}
	
	@Test
	void deleteAll_NotVoidTable_DoesNotThrowExceptions() {
		personajeRepository.save(new PersonajeEntity());
		
		assertDoesNotThrow(() -> personajeService.deleteAll());
		assertEquals(0L, personajeRepository.count());
	}
	
	@Test
	void update_WithIdNull_ThrowIdNullException() {
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		assertThrows(IdNullException.class, () -> personajeService.update(personajeDTOPATCH));
	}
	
	@Test
	void update_WithNotExistingId_ThrowPersonajeEntityNotFoundException() {
		Long NotExistingIdTestUpdate = 99L;
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		personajeDTOPATCH.setId(NotExistingIdTestUpdate);
		
		assertThrows(EntityNotFoundException.class, () -> personajeService.update(personajeDTOPATCH));
	}
	
	@Test
	void update_WithExistingId_ReturnPersonajeDTO() {
		PersonajeEntity personajeEntity = personajeRepository.save(new PersonajeEntity());
		
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		personajeDTOPATCH.setId(personajeEntity.getId());
		personajeDTOPATCH.setEdad(29);
		
		PersonajeDTO resultado = personajeService.update(personajeDTOPATCH);
		
		assertEquals(personajeDTOPATCH.getEdad(), personajeRepository.findById(personajeEntity.getId()).get().getEdad());
		assertEquals(personajeDTOPATCH.getEdad(), resultado.getEdad());
	}
}
