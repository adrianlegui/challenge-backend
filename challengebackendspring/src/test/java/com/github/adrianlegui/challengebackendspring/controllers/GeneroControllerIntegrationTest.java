package com.github.adrianlegui.challengebackendspring.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPOST;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOId;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOId;
import com.github.adrianlegui.challengebackendspring.entities.PeliculaEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@SpringBootTest(classes = { MappersConfig.class })
@AutoConfigureMockMvc(addFilters = false)
class GeneroControllerIntegrationTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	PeliculaRepository peliculaRepository;

	@Autowired
	SerieRepository serieRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void create_PostRequestWithNotExistedPeliculaId_ResponseNotFound()
		throws Exception {
		String uri = "/genres";

		PeliculaEntity notExistedPelicula = peliculaRepository
			.save(new PeliculaEntity());
		Long notExistdPeliculaId = notExistedPelicula.getId();
		peliculaRepository.delete(notExistedPelicula);

		PeliculaDTOId peliculaDTOId = new PeliculaDTOId();
		peliculaDTOId.setId(notExistdPeliculaId);

		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getPeliculas().add(peliculaDTOId);

		mockMvc
			.perform(post(uri).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(
					objectMapper.writeValueAsString(generoDTOPOST)))
			.andExpect(status().isNotFound());

	}

	@Test
	void create_PostRequestWithNotExistedSerieId_ResponseNotFound()
		throws Exception {
		SerieEntity notExistedSerie = serieRepository
			.save(new SerieEntity());
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);

		SerieDTOId serieDTOId = new SerieDTOId();
		serieDTOId.setId(notExistedSerieId);

		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getSeries().add(serieDTOId);

		String uri = "/genres";

		mockMvc
			.perform(post(uri).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(
					objectMapper.writeValueAsString(generoDTOPOST)))
			.andExpect(status().isNotFound());
	}

	@Test
	void create_PostRequestWithExistedPeliculaIdAndSerieId_ResponseCreated()
		throws Exception {
		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();

		PeliculaDTOId peliculaDTOId = new PeliculaDTOId();
		PeliculaEntity peliculaEntity = peliculaRepository
			.save(new PeliculaEntity());
		peliculaDTOId.setId(peliculaEntity.getId());
		generoDTOPOST.getPeliculas().add(peliculaDTOId);

		SerieDTOId serieDTOId = new SerieDTOId();
		SerieEntity serieEntity = serieRepository
			.save(new SerieEntity());
		serieDTOId.setId(serieEntity.getId());
		generoDTOPOST.getSeries().add(serieDTOId);

		String uri = "/genres";

		mockMvc
			.perform(post(uri).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(
					objectMapper.writeValueAsString(generoDTOPOST)))
			.andExpect(status().isCreated());
	}

}
