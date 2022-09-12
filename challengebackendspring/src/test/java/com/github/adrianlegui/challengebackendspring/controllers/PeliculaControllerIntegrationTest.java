package com.github.adrianlegui.challengebackendspring.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTO;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPOST;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOId;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;
import com.github.adrianlegui.challengebackendspring.entities.PeliculaEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.services.Orden;

@SpringBootTest(classes = {MappersConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class PeliculaControllerIntegrationTest {
	@Autowired
	MockMvc mockMvc;
	
	String controllerURI = "/movies";
	
	@Autowired
	PersonajeRepository personajeRepository;
	
	@Autowired
	GeneroRepository generoRepository;
	
	@Autowired
	PeliculaRepository peliculaRepository;

	@Autowired
	ObjectMapper objectMapper;
	
	@Test
	void create_PostRequestWithNotExistedPersonajeId_ResponseNotFound() throws Exception {
		Long notExistPersonajeId;
		PersonajeEntity notExistPersonaje = personajeRepository.save(new PersonajeEntity());
		notExistPersonajeId = notExistPersonaje.getId();
		personajeRepository.deleteById(notExistPersonajeId);
		
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		PersonajeDTOId personajeDTOId = new PersonajeDTOId();
		personajeDTOId.setId(notExistPersonajeId);
		peliculaDTOPOST.getPersonajesEnPelicula().add(personajeDTOId);
		
		mockMvc.perform(
				post(controllerURI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(
						objectMapper.writeValueAsString(peliculaDTOPOST)
						)
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void create_PostRequestWithNotExistedGeneroId_ResponseNotFound() throws Exception {
		Long notExistGeneroId;
		GeneroEntity notExistGenero = generoRepository.save(new GeneroEntity());
		notExistGeneroId = notExistGenero.getId();
		generoRepository.deleteById(notExistGeneroId);
		
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		GeneroDTOId generoDTOId = new GeneroDTOId();
		generoDTOId.setId(notExistGeneroId);
		peliculaDTOPOST.getGenerosDeLaPelicula().add(generoDTOId);
		
		mockMvc.perform(
				post(controllerURI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(
						objectMapper.writeValueAsString(peliculaDTOPOST)
						)
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void create_PostRequestWithBodyPeliculaDTOPOST_ResponseCreateWithBodyPeliculaDTO() throws Exception {
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		PersonajeDTOId personajeDTOId = new PersonajeDTOId();
		personajeDTOId.setId(personaje.getId());
		
		GeneroEntity genero = generoRepository.save(new GeneroEntity());
		GeneroDTOId generoDTOId = new GeneroDTOId();
		generoDTOId.setId(genero.getId());
		
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		peliculaDTOPOST.getGenerosDeLaPelicula().add(generoDTOId);
		peliculaDTOPOST.getPersonajesEnPelicula().add(personajeDTOId);
		
		MvcResult resultado = mockMvc.perform(
				post(controllerURI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(
						objectMapper.writeValueAsString(peliculaDTOPOST)
						)
				)
		.andExpect(status().isCreated())
		.andReturn();
		
		PeliculaDTO peliculaDTO = objectMapper.readValue(
				resultado.getResponse().getContentAsString(),
				PeliculaDTO.class
				);
		
		assertTrue(peliculaRepository.existsById(peliculaDTO.getId()));
	}
	
	@Test
	void asociarPersonajeAPelicula_PostRequestWithNotExistedPersonajeId_ResponseNotFound() throws Exception {
		String URI = controllerURI + "/{movieId}/characters/{characterId}";
		
		PeliculaEntity peliculaExistente = peliculaRepository.save(new PeliculaEntity());
		
		Long notExistPersonajeId;
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		notExistPersonajeId = personaje.getId();
		personajeRepository.deleteById(notExistPersonajeId);
		
		mockMvc.perform(
				post(URI, peliculaExistente.getId(), notExistPersonajeId)
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void asociarPersonajeAPelicula_PostRequestWithNotExistedPeliculaId_ResponseNotFound() throws Exception {
		String URI = controllerURI + "/{movieId}/characters/{characterId}";
		
		Long notExistPeliculaId;
		PeliculaEntity notExistPelicula = peliculaRepository.save(new PeliculaEntity());
		notExistPeliculaId = notExistPelicula.getId();
		peliculaRepository.deleteById(notExistPeliculaId);
		
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		
		mockMvc.perform(
				post(URI, notExistPeliculaId, personaje.getId())
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	@Transactional
	void asociarPersonajeAPelicula_PostRequestWithExistedPersonajeIdAndExistedPeliculaId_ResponseCreated() throws Exception {
		String URI = controllerURI + "/{movieId}/characters/{characterId}";
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());;
		
		mockMvc.perform(
				post(URI, pelicula.getId(), personaje.getId())
				)
		.andExpect(status().isCreated());
		
		assertTrue(
				peliculaRepository.findById(pelicula.getId())
				.get().getPersonajesEnPelicula().contains(personaje)
				);
	}
	
	@Test
	void update_PatchRequestWithPeliculaDTOPATCHWithIdNull_ResponseBadRequest() throws Exception{
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		
		mockMvc.perform(
				patch(controllerURI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(
						objectMapper.writeValueAsString(peliculaDTOPATCH)
						)
				)
		.andExpect(status().isBadRequest());
	}
	
	@Test
	void update_PatchRequestWithPeliculaDTOPATCHWithNotExistedPeliculaId_ResponseNotFound() throws Exception{
		PeliculaEntity notExistPelicula = peliculaRepository.save(new PeliculaEntity());
		Long notExistPeliculaId = notExistPelicula.getId();
		peliculaRepository.deleteById(notExistPeliculaId);
		
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		peliculaDTOPATCH.setId(notExistPeliculaId);
		
		mockMvc.perform(
				patch(controllerURI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(
						objectMapper.writeValueAsString(peliculaDTOPATCH)
						)
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void update_PatchRequestWithPeliculaDTOPATCH_ResponseOk() throws Exception{
		String tituloAntiguo = "titulo antiguo";
		PeliculaEntity peliculaEntity = peliculaRepository.save(new PeliculaEntity());
		peliculaEntity.setTitulo(tituloAntiguo);
		peliculaRepository.save(peliculaEntity);
		
		String tituloNuevo = "titulo nuevo";
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		peliculaDTOPATCH.setId(peliculaEntity.getId());
		peliculaDTOPATCH.setTitulo(tituloNuevo);
		
		MvcResult mvcResult = mockMvc.perform(
				patch(controllerURI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(
						objectMapper.writeValueAsString(peliculaDTOPATCH)
						)
				)
		.andExpect(status().isOk())
		.andReturn();
		
		PeliculaDTO peliculaDTO = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				PeliculaDTO.class
				);
		
		assertEquals(tituloNuevo, peliculaDTO.getTitulo());
		assertEquals(
				tituloNuevo,
				peliculaRepository.findById(peliculaEntity.getId()).get().getTitulo()
				);
	}
	
	@Test
	void findById_GetRequestWithNotExistedPeliculaId_ResponseNotFound() throws Exception {
		PeliculaEntity notExistPelicula = peliculaRepository.save(new PeliculaEntity());
		Long notExistedPeliculaId = notExistPelicula.getId();
		peliculaRepository.deleteById(notExistedPeliculaId);
		
		String URI = controllerURI + "/{movieId}";
		
		mockMvc.perform(
				get(URI, notExistedPeliculaId)
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void findById_GetRequestWithExistedPeliculaId_ResponseOKWithContentPeliculaDTOGET() throws Exception {
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		Long existedPeliculaId = pelicula.getId();
		
		String URI = controllerURI + "/{movieId}";
		
		mockMvc.perform(
				get(URI, existedPeliculaId)
				)
		.andExpect(status().isOk());
	}
	
	@Test
	void findAll_GetRequestWithNotExistedName_ResponseOkWithContentVoidList() throws Exception {
		String notExistedName = "not existed name";
		
		MvcResult mvcResult = mockMvc.perform(
				get(controllerURI)
				.param("name", notExistedName)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		List<PeliculaDTO> contenido = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<PeliculaDTO>>(){});
		
		assertTrue(contenido.isEmpty());
	}
	
	@Test
	void findAll_GetRequestWithExistedName_ResponseOkWithContentListWithOnePelicula() throws Exception {
		String existedName = "existed name";
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		pelicula.setTitulo(existedName);
		peliculaRepository.save(pelicula);
		
		MvcResult mvcResult = mockMvc.perform(
				get(controllerURI)
				.param("name", existedName)
				)
				.andExpect(status().isOk())
				.andReturn();
		
		List<PeliculaDTO> contenido = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<PeliculaDTO>>(){});
		
		assertEquals(1, contenido.size());
		assertEquals(existedName, contenido.get(0).getTitulo());
	}
	
	@Test
	void findAll_GetRequestWithNotExistedGenre_ResponseOkWithContentVoidList() throws Exception {
		GeneroEntity generoEntity = generoRepository.save(new GeneroEntity());
		Long notExistedGenre = generoEntity.getId();
		
		MvcResult mvcResult = mockMvc.perform(
				get(controllerURI)
				.param("genre", String.valueOf(notExistedGenre))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		List<PeliculaDTO> contenido = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<PeliculaDTO>>(){});
		
		assertTrue(contenido.isEmpty());
	}
	
	@Test
	void findAll_GetRequestWithExistedGenre_ResponseOkWithContentListWithOnePelicula() throws Exception {
		GeneroEntity generoEntity = generoRepository.save(new GeneroEntity());
		Long existedGenre = generoEntity.getId();
		
		PeliculaEntity peliculaEntity = peliculaRepository.save(new PeliculaEntity());
		peliculaEntity.getGenerosDeLaPelicula().add(generoEntity);
		peliculaRepository.save(peliculaEntity);
		
		MvcResult mvcResult = mockMvc.perform(
				get(controllerURI)
				.param("genre", String.valueOf(existedGenre))
				)
				.andExpect(status().isOk())
				.andReturn();
		
		List<PeliculaDTO> contenido = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<PeliculaDTO>>(){});
		
		assertEquals(1, contenido.size());
		assertEquals(peliculaEntity.getId(), contenido.get(0).getId());
	}
	
	@Test
	void findAll_GetRequestWithExistedNameAndOrderDesc_ResponseOkWithContentListWithTwoPeliculas() throws Exception {
		String existedName = "order desc";
		
		PeliculaEntity pelicula1 = new PeliculaEntity();
		pelicula1.setTitulo(existedName);
		pelicula1.setFechaDeCreacion(LocalDate.now());
		peliculaRepository.save(pelicula1);
		
		PeliculaEntity pelicula2 = new PeliculaEntity();
		pelicula2.setTitulo(existedName);
		pelicula2.setFechaDeCreacion(LocalDate.now().plusDays(1L));
		peliculaRepository.save(pelicula2);
		
		MvcResult mvcResult = mockMvc.perform(
				get(controllerURI)
				.param("name", String.valueOf(existedName))
				.param("order", Orden.DESC.toString())
				)
				.andExpect(status().isOk())
				.andReturn();
		
		List<PeliculaDTO> contenido = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<PeliculaDTO>>(){});
		
		assertEquals(2, contenido.size());
		assertEquals(pelicula2.getId(), contenido.get(0).getId());
		assertEquals(existedName, contenido.get(0).getTitulo());
	}
	
	@Test
	void desasociarPersonajeAPelicula_DeleteRequestWithNotExistedPeliculaId_ResponseNotFound() throws Exception{
		String URI = controllerURI + "/{movieId}/characters/{characterId}";
		
		Long notExistPeliculaId;
		PeliculaEntity notExistPelicula = peliculaRepository.save(new PeliculaEntity());
		notExistPeliculaId = notExistPelicula.getId();
		peliculaRepository.deleteById(notExistPeliculaId);
		
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		
		mockMvc.perform(
				delete(URI, notExistPeliculaId, personaje.getId())
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void desasociarPersonajeAPelicula_DeleteRequestWithNotExistedAssociation_ResponseBadRequest() throws Exception{
		String URI = controllerURI + "/{movieId}/characters/{characterId}";
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());

		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		
		mockMvc.perform(
				delete(URI, pelicula.getId(), personaje.getId())
				)
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@Transactional
	void desasociarPersonajeAPelicula_DeleteRequestWithExistedPeliculaAndAssociation_ResponseNoContent() throws Exception{
		String URI = controllerURI + "/{movieId}/characters/{characterId}";

		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		pelicula.getPersonajesEnPelicula().add(personaje);
		peliculaRepository.save(pelicula);
		
		mockMvc.perform(
				delete(URI, pelicula.getId(), personaje.getId())
				)
		.andExpect(status().isNoContent());
		
		Optional<PeliculaEntity> peliculaOptional = peliculaRepository.findById(pelicula.getId());
		
		PeliculaEntity resultado = peliculaOptional.get();
		
		assertFalse(resultado.getPersonajesEnPelicula().contains(personaje));
		assertTrue(resultado.getPersonajesEnPelicula().isEmpty());
	}
	
	@Test
	void deleteById_DeleteRequestWithNotExistedPeliculaId_ResponseNotFound() throws Exception{
		String URI = controllerURI + "/{movieId}";
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		Long notExistedPeliculaId = pelicula.getId();
		peliculaRepository.deleteById(notExistedPeliculaId);
		
		mockMvc.perform(
				delete(URI, notExistedPeliculaId)
				)
		.andExpect(status().isNotFound());
	}
	
	@Test
	void deleteById_DeleteRequestWithExistedPeliculaId_ResponseNoContent() throws Exception {
		String URI = controllerURI + "/{movieId}";
		
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		GeneroEntity genero = generoRepository.save(new GeneroEntity());
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		pelicula.getGenerosDeLaPelicula().add(genero);
		pelicula.getPersonajesEnPelicula().add(personaje);
		peliculaRepository.save(pelicula);
		
		mockMvc.perform(
				delete(URI, pelicula.getId())
				)
		.andExpect(status().isNoContent());
		
		assertFalse(peliculaRepository.existsById(pelicula.getId()));
	}
	
	@Test
	@Transactional
	void deleteAll_DeleteRequestWithVoidTable_ResponseBadRequest() throws Exception {
		if(0 < peliculaRepository.count()) {
			List<PeliculaEntity> peliculaEntities = peliculaRepository.findAll();
			
			for(PeliculaEntity pelicula : peliculaEntities) {
				pelicula.removeAllGenero();
				pelicula.removeAllPersonaje();
				peliculaRepository.save(pelicula);
			}
			peliculaRepository.deleteAll();
		}
		
		mockMvc.perform(
				delete(controllerURI)
				)
		.andExpect(status().isBadRequest());
	}
	
	@Test
	void deleteAll_DeleteRequestWithNoVoidTable_ResponseNoContent() throws Exception {
		PersonajeEntity personaje = personajeRepository.save(new PersonajeEntity());
		GeneroEntity genero = generoRepository.save(new GeneroEntity());
		
		PeliculaEntity pelicula = peliculaRepository.save(new PeliculaEntity());
		pelicula.getGenerosDeLaPelicula().add(genero);
		pelicula.getPersonajesEnPelicula().add(personaje);
		peliculaRepository.save(pelicula);
		
		mockMvc.perform(
				delete(controllerURI)
				)
		.andExpect(status().isNoContent());
	}
}
