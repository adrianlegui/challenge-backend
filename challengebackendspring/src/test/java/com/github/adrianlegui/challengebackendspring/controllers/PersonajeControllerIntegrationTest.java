package com.github.adrianlegui.challengebackendspring.controllers;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTO;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.PeliculaEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;
import com.github.adrianlegui.challengebackendspring.services.PersonajeService;


@SpringBootTest(classes = {MappersConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class PersonajeControllerIntegrationTest {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	PersonajeRepository personajeRepository;
	@Autowired
	PeliculaRepository peliculaRepository;
	@Autowired
	SerieRepository serieRepository;
	
	@Autowired
	PersonajeService personajeService;

	PersonajeDTOPOST personajeDTOPOST;
	
	PersonajeDTOPATCH personajeDTOPATCH;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		
		personajeDTOPOST = new PersonajeDTOPOST();
		
		personajeDTOPATCH = new PersonajeDTOPATCH();
	}
	
	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void create_PostRequestWithBodyPersonajeDTOPOST_ReturnCreatedWithContentPersonajeDTO() throws Exception {
		MvcResult resultado = mockMvc.perform(
				post("/characters")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(personajeDTOPOST))
				)
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id", notNullValue()))
		.andReturn();
		
		PersonajeDTO respuesta = objectMapper.readValue(
				resultado.getResponse().getContentAsString(),
				PersonajeDTO.class);
		
		assertTrue(personajeRepository.existsById(respuesta.getId()));
	}

	@Test
	void update_PatchRequestWithPersonajeDTOPATCH_ReturnOkWithContentPersonajeDTO() throws Exception{
		PersonajeEntity personajeExistente = personajeRepository.save(new PersonajeEntity());
		
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		personajeDTOPATCH.setId(personajeExistente.getId());
		personajeDTOPATCH.setEdad(25);
		personajeDTOPATCH.setHistoria("nueva historia");
		
		MvcResult resultado = mockMvc.perform(
				patch("/characters")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(personajeDTOPATCH))
				)
		.andExpect(status().isOk())
		.andReturn();
		
		PersonajeDTO respuesta = objectMapper.readValue(
				resultado.getResponse().getContentAsString(),
				PersonajeDTO.class);
		
		assertEquals(personajeDTOPATCH.getEdad(), respuesta.getEdad());
		assertEquals(personajeDTOPATCH.getHistoria(), respuesta.getHistoria());
	}
	
	@Test
	void update_PatchRequestWithPersonajeDTOPATCHWithIdNull_returnBadRequest() throws Exception{
		mockMvc.perform(
				patch("/characters")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(personajeDTOPATCH))
				)
		.andExpect(status().isBadRequest());
	}
	
	@Test
	void update_PatchRequestWithPersonajeDTOPATCHWithNotExistingId_ReturnNotFound() throws Exception{
		Long idNotFound = 1L;
		if(personajeRepository.existsById(idNotFound))
			personajeRepository.deleteById(idNotFound);
		
		personajeDTOPATCH.setId(idNotFound);
		
		mockMvc.perform(
				patch("/characters")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(personajeDTOPATCH))
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void deleteById_DeleteRequestWithNotExistingId_ReturnNotFound() throws Exception{
		Long idNotFound = 1L;
		if(personajeRepository.existsById(idNotFound))
			personajeRepository.deleteById(idNotFound);
		
		mockMvc.perform(
				delete("/characters/{id}", String.valueOf(idNotFound))
				.characterEncoding("utf-8")
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void deleteById_DeleteRequestWithExistingId_ReturnNoContent() throws Exception{
		PersonajeEntity personajeExistente = personajeRepository.save(new PersonajeEntity());
		
		PeliculaEntity peliculaCreada = peliculaRepository.save(new PeliculaEntity());
		peliculaCreada.addPersonaje(personajeExistente);
		peliculaRepository.save(peliculaCreada);
		
		SerieEntity serieCreada = serieRepository.save(new SerieEntity());
		serieCreada.addPersonaje(personajeExistente);
		serieRepository.save(serieCreada);
		
		mockMvc.perform(
				delete("/characters/{id}", String.valueOf(personajeExistente.getId()))
				)
		.andExpect(status().isNoContent());
	}
	
	@Test
	@Transactional
	void deleteAll_DeleteRequestWithVoidTable_ReturnBadRequest() throws Exception{
		if(0 < personajeRepository.count()) {
			List<PersonajeEntity> personajeEntities = personajeRepository.findAll();
			
			for(PersonajeEntity personaje : personajeEntities) {
				personaje.removeAllPelicula();;
				personaje.removeAllSerie();
				personajeRepository.save(personaje);
			}
			personajeRepository.deleteAll();
		}
		
		mockMvc.perform(
				delete("/characters")
				)
		.andExpect(status().isBadRequest());
	}
	
	@Test
	void deleteAll_DeleteRequestWithNotVoidTable_ReturnNoContent() throws Exception{
		PersonajeEntity personajeEntity = new PersonajeEntity();
		personajeRepository.save(personajeEntity);
		
		mockMvc.perform(
				delete("/characters")
				)
		.andExpect(status().isNoContent());
	}
	
	@Test
	void findById_GetRequestWithNotExistingId_ReturnNotFound() throws Exception{
		Long idNotFound = 1L;
		if(personajeRepository.existsById(idNotFound))
			personajeRepository.deleteById(idNotFound);
		
		mockMvc.perform(
				get("/characters/{id}", String.valueOf(idNotFound))
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void findById_GetRequestWithExistingId_ReturnOk() throws Exception{
		Long idFound = personajeRepository.save(new PersonajeEntity()).getId();
		
		mockMvc.perform(
				get("/characters/{id}", String.valueOf(idFound))
				)
		.andExpect(status().isOk());
	}
	
	@Test
	void findAll_GetRequestWithParamsNameAgeMoviesSeries_ReturnOk() throws Exception{
		SerieEntity serieEntity = serieRepository.save(new SerieEntity());
		PeliculaEntity peliculaEntity = peliculaRepository.save(new PeliculaEntity());
		
		Integer edad = 20;
		String name = "nombre";
		String age = String.valueOf(edad);
		String movies = String.valueOf(peliculaEntity.getId());
		String series = String.valueOf(serieEntity.getId());
		
		PersonajeEntity personajeEntity = personajeRepository.save(new PersonajeEntity());
		personajeEntity.setNombre(name);
		personajeEntity.setEdad(edad);
		
		personajeRepository.save(personajeEntity);
		
		serieEntity.addPersonaje(personajeEntity);
		serieRepository.save(serieEntity);
		
		peliculaEntity.addPersonaje(personajeEntity);
		peliculaRepository.save(peliculaEntity);
		
		MvcResult resultado = mockMvc.perform(
				get("/characters?name={name}&age={age}&movies={movies}&series{series}",
						name, age, movies, series)
				)
		.andExpect(status().isOk())
		.andReturn();
		
		List<PersonajeDTO> respuesta = objectMapper.readValue(
				resultado.getResponse().getContentAsString(),
				new TypeReference<List<PersonajeDTO>>(){});
		
		assertFalse(respuesta.isEmpty());
		assertEquals(1, respuesta.size());
	}

}
