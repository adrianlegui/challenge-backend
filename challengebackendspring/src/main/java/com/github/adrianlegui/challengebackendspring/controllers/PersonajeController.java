package com.github.adrianlegui.challengebackendspring.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTO;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.PersonajeService;

@RestController
@RequestMapping(value = "/characters")
public class PersonajeController {
	@Autowired
	PersonajeService personajeService;
	
	@PostMapping
	public ResponseEntity<PersonajeDTO> create(@RequestBody PersonajeDTOPOST personajeDTOPOST){		
		PersonajeDTO personajeDTOResponse = personajeService.create(personajeDTOPOST);
		
		return new ResponseEntity<>(personajeDTOResponse, HttpStatus.CREATED);
	}
	
	@PatchMapping
	public ResponseEntity<PersonajeDTO> update(@RequestBody PersonajeDTOPATCH personajeDTOPATCH){
		PersonajeDTO personajeDTOResponse = personajeService.update(personajeDTOPATCH);

		return ResponseEntity.ok(personajeDTOResponse);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PersonajeDTOGET> findByID(@PathVariable("id")Long id) {
		return ResponseEntity.ok(personajeService.findById(id));
	}
	
	@GetMapping
	public ResponseEntity<List<PersonajeDTOGET>> findAll(
			@RequestParam(required = false, name = "name") String nombre,
			@RequestParam(required = false, name = "age") Integer edad,
			@RequestParam(required = false, name = "movies") Long idPelicula,
			@RequestParam(required = false, name = "series") Long idSerie){
		
		return ResponseEntity.ok(personajeService.findAll(nombre, edad, idPelicula, idSerie));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
		personajeService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteAll() {
		personajeService.deleteAll();
		return ResponseEntity.noContent().build();
	}
}
