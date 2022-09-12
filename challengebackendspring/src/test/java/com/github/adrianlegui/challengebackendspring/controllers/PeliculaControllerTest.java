package com.github.adrianlegui.challengebackendspring.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTO;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.Orden;
import com.github.adrianlegui.challengebackendspring.services.PeliculaService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {PeliculaController.class})
@ContextConfiguration(classes = {PeliculaController.class})
class PeliculaControllerTest {
	@MockBean
	PeliculaService peliculaService;
	
	@Autowired
	MockMvc mockMvc;
	
	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void create_PostRequestWithBodyPeliculaDTOPOST_ResponseCreatedWithBodyPeliculaDTO() throws Exception {
		String URI = "/movies";
		PeliculaDTOPOST peliculaDTOPOST = new PeliculaDTOPOST();
		PeliculaDTO peliculaDTO = new PeliculaDTO();
		when(peliculaService.create(peliculaDTOPOST)).thenReturn(peliculaDTO);
		
		mockMvc.perform(
				post(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(peliculaDTOPOST))
				)
		.andExpect(status().isCreated());
	}
	

	@Test
	void asociarPersonajeAPelicula_PostRequestWithURIWithMovieIdAndCharacterId_ResponseCreatedWithBodyPeliculaDTO() throws Exception {
		String URI = "/movies/{movieId}/characters/{characterId}";
		PeliculaDTO peliculaDTO = new PeliculaDTO();
		Long movieId = 1L;
		Long characterId = 1L;
		
		when(peliculaService.asociarPersonajeAPelicula(movieId, characterId)).thenReturn(peliculaDTO);
		
		mockMvc.perform(
				post(URI, movieId, characterId)
				)
		.andExpect(status().isCreated());
	}

	@Test
	void update_PatchRequestWithBodyPeliculaDTOPATCH_ResponseOkWithBodyPeliculaDTO() throws Exception{
		String URI = "/movies";
		PeliculaDTO peliculaDTO = new PeliculaDTO();
		PeliculaDTOPATCH peliculaDTOPATCH = new PeliculaDTOPATCH();
		
		when(peliculaService.update(peliculaDTOPATCH)).thenReturn(peliculaDTO);
		
		mockMvc.perform(
				patch(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(peliculaDTOPATCH))
				)
		.andExpect(status().isOk());
	}

	@Test
	void findById_GetRequestWithURIWithMovieId_ResponseOkWithBodyPeliculaDTOGET() throws Exception{
		String URI = "/movies/{movieId}";
		Long movieId = 1L;
		
		PeliculaDTOGET peliculaDTOGET = new PeliculaDTOGET();
		when(peliculaService.findById(anyLong())).thenReturn(peliculaDTOGET);
		
		mockMvc.perform(get(URI, movieId)).andExpect(status().isOk());
	}

	@Test
	void findAll_GetRequestWithAllParams_ResponseOkWithBodyListOfPeliculaDTOGET() throws Exception{
		String URI = "/movies";
		String name = "nombre";
		String genre = "1";
		String order = Orden.DESC.toString();
		
		List<PeliculaDTOGET> peliculaDTOGETs = new ArrayList<>();
		peliculaDTOGETs.add(new PeliculaDTOGET());
		
		when(peliculaService.findAll(anyString(),
				anyLong(),
				any()))
		.thenReturn(peliculaDTOGETs);
		
		mockMvc.perform(
				get(URI)
				.param("name", name)
				.param("genre", genre)
				.param("order", order)
				)
		.andExpect(status().isOk());
	}

	@Test
	void desasociarPersonajeAPelicula_DeleteRequestWithURIWithMovieIdAndCharacterId_ResponseNoContent() throws Exception{
		String URI = "/movies/{movieId}/characters/{characterId}";
		Long movieId = 1L;
		Long characterId = 1L;
		
		doNothing().when(peliculaService).desasociarPersonajeAPelicula(movieId, characterId);
		
		mockMvc.perform(
				delete(URI, movieId, characterId)
				)
		.andExpect(status().isNoContent());
	}

	@Test
	void deleteById_DeleteRequestWithURIWithMovieId_ResponseNoContent() throws Exception {
		String URI = "/movies/{movieId}";
		Long movieId = 1L;
		
		doNothing().when(peliculaService).deleteById(anyLong());
		
		mockMvc.perform(
				delete(URI, movieId)
				)
		.andExpect(status().isNoContent());
	}

	@Test
	void deleteAll_DeleteRequest_ResponseNoContent() throws Exception{
		String URI = "/movies";
		
		doNothing().when(peliculaService).deleteAll();
		
		mockMvc.perform(delete(URI)).andExpect(status().isNoContent());
	}

}
