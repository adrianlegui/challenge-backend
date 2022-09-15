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
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOId;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;
import com.github.adrianlegui.challengebackendspring.services.Orden;

@SpringBootTest(classes = { MappersConfig.class })
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class SerieControllerIntegrationTest {
	@Autowired
	MockMvc mockMvc;

	String URI = "/series";

	@Autowired
	PersonajeRepository personajeRepository;

	@Autowired
	GeneroRepository generoRepository;

	@Autowired
	SerieRepository serieRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void create_PostRequestWithNotExistedPersonajeId_ResponseNotFound()
		throws Exception {
		Long notExistPersonajeId;
		PersonajeEntity notExistPersonaje = personajeRepository
			.save(new PersonajeEntity());
		notExistPersonajeId = notExistPersonaje.getId();
		personajeRepository.deleteById(notExistPersonajeId);

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		PersonajeDTOId personajeDTOId = new PersonajeDTOId();
		personajeDTOId.setId(notExistPersonajeId);
		serieDTOPOST.getPersonajesEnSerie().add(personajeDTOId);

		mockMvc
			.perform(
				post(URI)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8")
					.content(
						objectMapper.writeValueAsString(serieDTOPOST)))
			.andExpect(status().isNotFound());
	}

	@Test
	void create_PostRequestWithNotExistedGeneroId_ResponseNotFound()
		throws Exception {
		GeneroEntity notExistGenero = generoRepository
			.save(new GeneroEntity());
		
		Long notExistGeneroId = notExistGenero.getId();
		
		generoRepository.delete(notExistGenero);

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		GeneroDTOId generoDTOId = new GeneroDTOId();
		generoDTOId.setId(notExistGeneroId);
		serieDTOPOST.getGenerosDeLaSerie().add(generoDTOId);

		mockMvc
			.perform(
				post(URI)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8")
					.content(
						objectMapper.writeValueAsString(serieDTOPOST)))
			.andExpect(status().isNotFound());
	}

	@Test
	void create_PostRequestWithBodySerieDTOPOST_ResponseCreateWithBodySerieDTO()
		throws Exception {
		PersonajeEntity personaje = personajeRepository
			.save(new PersonajeEntity());
		PersonajeDTOId personajeDTOId = new PersonajeDTOId();
		personajeDTOId.setId(personaje.getId());

		GeneroEntity genero = generoRepository.save(new GeneroEntity());
		GeneroDTOId generoDTOId = new GeneroDTOId();
		generoDTOId.setId(genero.getId());

		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();
		serieDTOPOST.getPersonajesEnSerie().add(personajeDTOId);
		serieDTOPOST.getGenerosDeLaSerie().add(generoDTOId);

		MvcResult mvcResult = mockMvc
			.perform(
				post(URI)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8")
					.content(
						objectMapper.writeValueAsString(serieDTOPOST)))
			.andExpect(status().isCreated())
			.andReturn();

		SerieDTO resultado = objectMapper
			.readValue(
				mvcResult.getResponse().getContentAsString(),
				SerieDTO.class);

		assertTrue(serieRepository.existsById(resultado.getId()));
	}

	@Test
	void asociarPersonajeASerie_PostRequestWithNotExistedPersonajeId_ResponseNotFound()
		throws Exception {
		String associationURI = URI
			+ "/{serieId}/characters/{characterId}";

		PersonajeEntity notExistedPersonaje = personajeRepository
			.save(new PersonajeEntity());
		Long notExistedPersonajeId = notExistedPersonaje.getId();
		personajeRepository.delete(notExistedPersonaje);

		SerieEntity existedSerie = serieRepository
			.save(new SerieEntity());
		Long existedSerieId = existedSerie.getId();

		mockMvc
			.perform(
				post(
					associationURI,
					existedSerieId,
					notExistedPersonajeId))
			.andExpect(status().isNotFound());
	}

	@Test
	void asociarPersonajeASerie_PostRequestWithNotExistedSerieId_ResponseNotFound()
		throws Exception {
		String associationURI = URI
			+ "/{serieId}/characters/{characterId}";

		PersonajeEntity existedPersonaje = personajeRepository
			.save(new PersonajeEntity());
		Long existedPersonajeId = existedPersonaje.getId();

		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);

		mockMvc
			.perform(
				post(
					associationURI,
					notExistedSerieId,
					existedPersonajeId))
			.andExpect(status().isNotFound());
	}

	@Test
	void asociarPersonajeASerie_PostRequestWithExistedPersonajeIdAndExistedSerieId_ResponseCreated()
		throws Exception {
		String associationURI = URI
			+ "/{serieId}/characters/{characterId}";

		PersonajeEntity existedPersonaje = personajeRepository
			.save(new PersonajeEntity());
		Long existedPersonajeId = existedPersonaje.getId();

		SerieEntity existedSerie = serieRepository
			.save(new SerieEntity());
		Long existedSerieId = existedSerie.getId();

		mockMvc
			.perform(
				post(
					associationURI,
					existedSerieId,
					existedPersonajeId))
			.andExpect(status().isCreated());

		assertTrue(
			serieRepository
				.findById(existedSerieId)
				.get()
				.hasPersonajeWithId(existedPersonajeId));
	}

	@Test
	void update_PatchRequestWithSerieDTOPATCHWithIdNull_ResponseBadRequest()
		throws Exception {
		SerieDTOPATCH serieDTOPATCHwithIdNull = new SerieDTOPATCH();

		mockMvc
			.perform(
				patch(URI)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8")
					.content(
						objectMapper
							.writeValueAsString(
								serieDTOPATCHwithIdNull)))
			.andExpect(status().isBadRequest());
	}

	@Test
	void update_PatchRequestWithSerieDTOPATCHWithNotExistedSerieId_ResponseNotFound()
		throws Exception {
		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());

		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);

		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();
		serieDTOPATCH.setId(notExistedSerieId);

		mockMvc
			.perform(
				patch(URI)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8")
					.content(
						objectMapper.writeValueAsString(serieDTOPATCH)))
			.andExpect(status().isNotFound());
	}

	@Test
	void update_PatchRequestWithSerieDTOPATCH_ResponseOK()
		throws Exception {
		String tituloAntiguo = "titulo antiguo";
		SerieEntity serieEntity = serieRepository
			.save(new SerieEntity());
		serieEntity.setTitulo(tituloAntiguo);
		serieEntity = serieRepository.save(serieEntity);

		String tituloNuevo = "titulo nuevo";
		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();
		serieDTOPATCH.setId(serieEntity.getId());
		serieDTOPATCH.setTitulo(tituloNuevo);

		MvcResult mvcResult = mockMvc
			.perform(
				patch(URI)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8")
					.content(
						objectMapper.writeValueAsString(serieDTOPATCH)))
			.andExpect(status().isOk())
			.andReturn();

		SerieDTO resultado = objectMapper
			.readValue(
				mvcResult.getResponse().getContentAsString(),
				SerieDTO.class);

		assertEquals(tituloNuevo, resultado.getTitulo());
		assertEquals(
			tituloNuevo,
			serieRepository
				.findById(serieEntity.getId())
				.get()
				.getTitulo());
	}

	@Test
	void findById_GetRequestWithNotExistedSerieId_ResponseNotFound()
		throws Exception {
		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);

		String findByIdUri = URI + "/{serieId}";

		mockMvc
			.perform(get(findByIdUri, notExistedSerieId))
			.andExpect(status().isNotFound());
	}

	@Test
	void findBydId_GetRequestWithExistedSerieId_ResponseOk()
		throws Exception {
		SerieEntity existedSerie = serieRepository
			.save(new SerieEntity());
		Long existedSerieId = existedSerie.getId();

		String findByIdUri = URI + "/{serieId}";

		mockMvc
			.perform(get(findByIdUri, existedSerieId))
			.andExpect(status().isOk());
	}

	@Test
	void findAll_GetRequestWithNotExistedName_ResponseOKWithContentWithEmptyList()
		throws Exception {
		String notExistedName = "not existed name";

		MvcResult mvcResult = mockMvc
			.perform(get(URI).param("name", notExistedName))
			.andExpect(status().isOk())
			.andReturn();

		List<SerieDTO> resultado = objectMapper
			.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<SerieDTO>>() {
				});

		assertTrue(resultado.isEmpty());
	}

	@Test
	void findAll_GetRequestWithExistedName_ResponseOkWithListWithOneSerie()
		throws Exception {
		String existedName = "existed name";
		SerieEntity serie = serieRepository.save(new SerieEntity());
		serie.setTitulo(existedName);
		serieRepository.save(serie);

		MvcResult mvcResult = mockMvc
			.perform(get(URI).param("name", existedName))
			.andExpect(status().isOk())
			.andReturn();

		List<SerieDTO> resultado = objectMapper
			.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<SerieDTO>>() {
				});

		assertFalse(resultado.isEmpty());
		assertEquals(1, resultado.size());
		assertEquals(existedName, resultado.get(0).getTitulo());
	}

	@Test
	void findAll_GetRequestWithNotExistedAssociationWithGeneroId_ResponseOkWithEmptyList()
		throws Exception {
		GeneroEntity generoEntity = generoRepository
			.save(new GeneroEntity());
		Long notExistedGenre = generoEntity.getId();

		MvcResult mvcResult = mockMvc
			.perform(
				get(URI)
					.param("genre", String.valueOf(notExistedGenre)))
			.andExpect(status().isOk())
			.andReturn();

		List<SerieDTO> resultado = objectMapper
			.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<SerieDTO>>() {
				});

		assertTrue(resultado.isEmpty());
	}

	@Test
	void findAll_GetRequestWithExistedAssociationWithGeneroId_ResponseOkWithListWithOneSerie()
		throws Exception {
		GeneroEntity generoEntity = generoRepository
			.save(new GeneroEntity());
		Long existedGenre = generoEntity.getId();

		SerieEntity serieEntity = serieRepository
			.save(new SerieEntity());
		serieEntity.getGenerosDeLaSerie().add(generoEntity);
		serieRepository.save(serieEntity);

		MvcResult mvcResult = mockMvc
			.perform(
				get(URI).param("genre", String.valueOf(existedGenre)))
			.andExpect(status().isOk())
			.andReturn();

		List<SerieDTO> resultado = objectMapper
			.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<SerieDTO>>() {
				});

		assertFalse(resultado.isEmpty());
		assertEquals(1, resultado.size());
		assertEquals(serieEntity.getId(), resultado.get(0).getId());
	}

	@Test
	void findAll_GetRequestWithExistedNameAndOrderDesc_ResponseOkWithContentListWithTwoSeries()
		throws Exception {
		String existedName = "order desc";

		SerieEntity serie1 = new SerieEntity();
		serie1.setTitulo(existedName);
		serie1.setFechaDeCreacion(LocalDate.now());
		serieRepository.save(serie1);

		SerieEntity serie2 = new SerieEntity();
		serie2.setTitulo(existedName);
		serie2.setFechaDeCreacion(LocalDate.now().plusDays(1L));
		serieRepository.save(serie2);

		MvcResult mvcResult = mockMvc
			.perform(
				get(URI)
					.param("name", String.valueOf(existedName))
					.param("order", Orden.DESC.toString()))
			.andExpect(status().isOk())
			.andReturn();

		List<SerieDTO> resultado = objectMapper
			.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<List<SerieDTO>>() {
				});

		assertEquals(2, resultado.size());
		assertEquals(serie2.getId(), resultado.get(0).getId());
		assertEquals(existedName, resultado.get(0).getTitulo());
	}

	@Test
	void desasociarPersonajeDeSerie_DeleteRequestWithNotExistedSerieId_ResponseNotFound()
		throws Exception {
		String desassociationURI = URI
			+ "/{serieId}/characters/{characterId}";

		SerieEntity notExistSerie = serieRepository
			.save(new SerieEntity());
		Long notExistSerieId = notExistSerie.getId();
		serieRepository.deleteById(notExistSerieId);

		PersonajeEntity personaje = personajeRepository
			.save(new PersonajeEntity());

		mockMvc
			.perform(
				delete(
					desassociationURI,
					notExistSerieId,
					personaje.getId()))
			.andExpect(status().isNotFound());
	}

	@Test
	void desasociarPersonajeDeSerie_DeleteRequestWithNotExistedAssociation_ResponseBadRequest()
		throws Exception {
		String desassociationURI = URI
			+ "/{serieId}/characters/{characterId}";
		PersonajeEntity personaje = personajeRepository
			.save(new PersonajeEntity());
		SerieEntity serie = serieRepository.save(new SerieEntity());

		mockMvc
			.perform(
				delete(
					desassociationURI,
					serie.getId(),
					personaje.getId()))
			.andExpect(status().isBadRequest());
	}

	@Test
	void desasociarPersonajeASerie_DeleteRequestWithExistedSerieAndAssociation_ResponseNoContent()
		throws Exception {
		String desassociationURI = URI
			+ "/{serieId}/characters/{characterId}";

		PersonajeEntity personaje = personajeRepository
			.save(new PersonajeEntity());
		SerieEntity serie = serieRepository.save(new SerieEntity());
		serie.addPersonaje(personaje);
		serie = serieRepository.save(serie);

		mockMvc
			.perform(
				delete(
					desassociationURI,
					serie.getId(),
					personaje.getId()))
			.andExpect(status().isNoContent());

		SerieEntity resultado = serieRepository
			.findById(serie.getId())
			.get();

		assertFalse(resultado.hasPersonajeWithId(personaje.getId()));
	}

	@Test
	void deleteById_DeleteRequestWithNotExistedSerieId_ResponseNotFound()
		throws Exception {
		String deleteByIdUri = URI + "/{serieId}";
		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);

		mockMvc
			.perform(delete(deleteByIdUri, notExistedSerieId))
			.andExpect(status().isNotFound());
	}

	@Test
	void deleteById_DeleteRequestWithExistedSerieIdWithAssociations_ResponseNotNoContent()
		throws Exception {
		String deleteByIdUri = URI + "/{serieId}";

		GeneroEntity genero = generoRepository.save(new GeneroEntity());
		PersonajeEntity personaje = personajeRepository
			.save(new PersonajeEntity());

		SerieEntity serie = serieRepository.save(new SerieEntity());
		serie.addGenero(genero);
		serie.addPersonaje(personaje);
		serie = serieRepository.save(serie);

		mockMvc
			.perform(delete(deleteByIdUri, serie.getId()))
			.andExpect(status().isNoContent());
	}

	@Test
	void deleteAll_DeleteRequestWithVoidTable_ResponseBadRequest()
		throws Exception {
		if (0 < serieRepository.count()) {
			List<SerieEntity> serieEntities = serieRepository.findAll();

			for (SerieEntity serie : serieEntities) {
				serie.removeAllGenero();
				serie.removeAllPersonaje();
				serieRepository.save(serie);
			}
			serieRepository.deleteAll();
		}
		mockMvc.perform(delete(URI)).andExpect(status().isBadRequest());
	}

	@Test
	void deleteAll_DeleteRequestWithNoVoidTable_ResponseNoContent()
		throws Exception {
		serieRepository.save(new SerieEntity());
		mockMvc.perform(delete(URI)).andExpect(status().isNoContent());
	}
}
