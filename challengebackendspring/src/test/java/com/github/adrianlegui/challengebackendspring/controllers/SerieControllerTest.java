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
import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.Orden;
import com.github.adrianlegui.challengebackendspring.services.SerieService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = { SerieController.class })
@ContextConfiguration(classes = { SerieController.class })
class SerieControllerTest {
	String URI = "/series";

	@MockBean
	SerieService serieService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void create_PostRequestWithBodySerieDTOPOST_ResponseCreated()
		throws Exception {
		SerieDTOPOST serieDTOPOST = new SerieDTOPOST();

		SerieDTO serieDTO = new SerieDTO();

		when(serieService.create(serieDTOPOST)).thenReturn(serieDTO);

		mockMvc
			.perform(
				post(URI).contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8").content(
						objectMapper.writeValueAsString(serieDTOPOST)))
			.andExpect(status().isCreated());
	}

	@Test
	void asociarPersonajeASerie_PostRequestWithUriWithSerieIdAndCharacterId_ResponseCreated()
		throws Exception {
		String associationUri = URI
			+ "/{serieId}/characters/{characterId}";
		SerieDTO serieDTO = new SerieDTO();

		Long serieId = 1L;
		Long characterId = 1L;

		when(serieService.asociarPersonajeASerie(serieId, characterId))
			.thenReturn(serieDTO);

		mockMvc.perform(post(associationUri, serieId, characterId))
			.andExpect(status().isCreated());
	}

	@Test
	void update_PatchRequestWithBodySerieDtoPatch_ResponseOk()
		throws Exception {
		SerieDTO serieDTO = new SerieDTO();
		SerieDTOPATCH serieDTOPATCH = new SerieDTOPATCH();

		when(serieService.update(serieDTOPATCH)).thenReturn(serieDTO);

		mockMvc
			.perform(
				patch(URI).contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8").content(
						objectMapper.writeValueAsString(serieDTOPATCH)))
			.andExpect(status().isOk());
	}

	@Test
	void findBydId_GetRequestWithUriWithSerieId_ResponseOk()
		throws Exception {
		String findUri = URI + "/{serieId}";

		SerieDTOGET serieDTOGET = new SerieDTOGET();

		Long serieId = 1L;

		when(serieService.findById(serieId)).thenReturn(serieDTOGET);

		mockMvc.perform(get(findUri, serieId))
			.andExpect(status().isOk());
	}

	@Test
	void findAll_GetRequestWithAllParams_ResponseOk() throws Exception {
		String name = "nombre";
		String genre = "1";
		String orden = Orden.DESC.toString();

		List<SerieDTOGET> serieDTOGETs = new ArrayList<>();

		when(serieService.findAll(anyString(), anyLong(), any()))
			.thenReturn(serieDTOGETs);

		mockMvc.perform(
			get(URI).param("name", name).param("genre", genre)
				.param("order", orden))
			.andExpect(status().isOk());
	}

	@Test
	void desasociarPersonajeDeSerie_DeleteRequestWithUriWithSerieIdAndCharacterId_ResponseNoContent()
		throws Exception {
		String desasociarUri = URI
			+ "/{serieId}/characters/{characterId}";

		Long serieId = 1L;
		Long characterId = 1L;

		doNothing().when(serieService)
			.desasociarPersonajeASerie(serieId, characterId);

		mockMvc.perform(delete(desasociarUri, serieId, characterId))
			.andExpect(status().isNoContent());
	}

	@Test
	void deleteById_DeleteRequestWithUriWithSerieId_ResponseNoContent()
		throws Exception {

		String deleteUri = URI + "/{serieId}";
		Long serieId = 1L;

		doNothing().when(serieService).deleteById(serieId);

		mockMvc.perform(delete(deleteUri, serieId))
			.andExpect(status().isNoContent());
	}

	@Test
	void deleteAll_DeleteRequest_ResponseNoContent() throws Exception {
		doNothing().when(serieService).deleteAll();

		mockMvc.perform(delete(URI)).andExpect(status().isNoContent());
	}

}
