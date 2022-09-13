package com.github.adrianlegui.challengebackendspring.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTO;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.GeneroService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = { GeneroController.class })
@ContextConfiguration(classes = { GeneroController.class })
class GeneroControllerTest {
	@MockBean
	GeneroService generoService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void create_PostRequestWithBodyGeneroDTOPOST_ResponseCreatedWithBodyGeneroDTO()
		throws Exception {
		String uri = "/genres";

		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();

		GeneroDTO generoDTO = new GeneroDTO();
		when(generoService.create(generoDTOPOST)).thenReturn(generoDTO);

		mockMvc
			.perform(post(uri).contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8").content(
					objectMapper.writeValueAsString(generoDTOPOST)))
			.andExpect(status().isCreated());
	}

}
