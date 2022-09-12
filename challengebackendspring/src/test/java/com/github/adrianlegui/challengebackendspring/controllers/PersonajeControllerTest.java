package com.github.adrianlegui.challengebackendspring.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTO;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.PersonajeService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = {PersonajeController.class})
@ContextConfiguration(classes = {PersonajeController.class})
class PersonajeControllerTest {
	@MockBean
	PersonajeService personajeService;
	
	@Autowired
	MockMvc mockMvc;
	
	ObjectMapper objectMapper = new ObjectMapper();

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
	void create_PostRequestWithBodyPersonajeDTOPOST_ReturnCreatedWithBodyPersonajeDTO() throws Exception{
		PersonajeDTOPOST personajeDTOPOST = new PersonajeDTOPOST();
		
		PersonajeDTO personajeDTO = new PersonajeDTO();
		when(personajeService.create(any(PersonajeDTOPOST.class))).thenReturn(personajeDTO );
		
		mockMvc.perform(
				post("/characters")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(personajeDTOPOST))
				)
		.andExpect(status().isCreated());
	}
	
	@Test
	void update_PatchRequestWithBodyPersonajedDTOPATCH_ReturnOkWithBodyPersonajeDTO() throws Exception{
		Long idUpdate = 1L;
		
		PersonajeDTOPATCH personajeDTOPATCH = new PersonajeDTOPATCH();
		personajeDTOPATCH.setId(idUpdate);
		
		PersonajeDTO personajeDTO = new PersonajeDTO();
		personajeDTO .setId(idUpdate);
		
		when(personajeService.update(any(PersonajeDTOPATCH.class))).thenReturn(personajeDTO);
		
		mockMvc.perform(patch("/characters")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(personajeDTOPATCH))
				)
		.andExpect(status().isOk());
	}
	
	@Test
	void findById_GetRequestWithVariableId_ReturnOkWithBodyPersonajeDTOGET() throws Exception{
		Long idFind = 1L;
		PersonajeDTOGET personajeDTOGET = new PersonajeDTOGET();
		
		when(personajeService.findById(anyLong())).thenReturn(personajeDTOGET);
		
		mockMvc.perform(get("/characters/" + idFind)).andExpect(status().isOk());
		
	}
	
	@Test
	void findAll_GetRequestWithParamsNameAgeMoviesSeries_ReturnOkWithBodyListOfPersonajeDTOGET() throws Exception {
		// recibe
		String name = "nombre";
		String age = "20";
		String movies = "1";
		String series = "1";
		
		PersonajeDTOGET personajeDTOGET = new PersonajeDTOGET();
		personajeDTOGET.setId(1l);
		personajeDTOGET.setNombre(name);
		
		List<PersonajeDTOGET> personajeDTOGETs = new ArrayList<>();
		personajeDTOGETs.add(personajeDTOGET);
		
		// mocking
		when(personajeService.findAll(
				any(String.class),
				any(Integer.class), 
				any(Long.class), 
				any(Long.class))
				).thenReturn(personajeDTOGETs);
		
		// test y assert
		mockMvc.perform(get("/characters")
				.param("name", name)
				.param("age", age)
				.param("movies", movies)
				.param("series", series)
				)
		.andExpect(status().isOk());
	}

	@Test
	void deleteById_DeleteRequestWithVariableIdInURI_ReturnNoContent() throws Exception{
		Long idDelete = 1L;
		
		doNothing().when(personajeService).deleteById(anyLong());
		
		mockMvc.perform(delete("/characters/" + idDelete)).andExpect(status().isNoContent());
	}
	
	@Test
	void deleteAll_DeleteRequest_ReturnNoContent() throws Exception {
		doNothing().when(personajeService).deleteAll();
		
		mockMvc.perform(delete("/characters")).andExpect(status().isNoContent());
	}
}
