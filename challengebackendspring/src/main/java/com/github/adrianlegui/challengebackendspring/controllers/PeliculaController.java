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

import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTO;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.Orden;
import com.github.adrianlegui.challengebackendspring.services.PeliculaService;

@RestController
@RequestMapping("/movies")
public class PeliculaController {
	@Autowired
	private PeliculaService peliculaService;
	
	// post request
	@PostMapping
	public ResponseEntity<PeliculaDTO> create(@RequestBody PeliculaDTOPOST peliculaDTOPOST) {
		PeliculaDTO peliculaDTO = peliculaService.create(peliculaDTOPOST);
		return new ResponseEntity<>(peliculaDTO, HttpStatus.CREATED);
	}
	
	@PostMapping("/{movieId}/characters/{characterId}")
	public ResponseEntity<PeliculaDTO> asociarPersonajeAPelicula(
			@PathVariable(name = "movieId") Long movieId,
			@PathVariable(name = "characterId") Long characterId) {
		PeliculaDTO peliculaDTO = peliculaService.asociarPersonajeAPelicula(
				movieId, 
				characterId
				);
		return new ResponseEntity<>(peliculaDTO, HttpStatus.CREATED);
	}
	
	// patch request
	@PatchMapping
	public ResponseEntity<PeliculaDTO> update(
			@RequestBody PeliculaDTOPATCH peliculaDTOPATCH){
		PeliculaDTO peliculaDTO = peliculaService.update(peliculaDTOPATCH);
		return new ResponseEntity<>(peliculaDTO, HttpStatus.OK);
	}
	
	// get request
	@GetMapping("/{movieId}")
	public ResponseEntity<PeliculaDTOGET> findById(
			@PathVariable(name = "movieId") Long movieId){
		return ResponseEntity.ok(peliculaService.findById(movieId));
	}
	
	@GetMapping
	public ResponseEntity<List<PeliculaDTOGET>> findAll(
			@RequestParam(required = false, name = "name") String name,
			@RequestParam(required = false, name = "genre") Long genreId,
			@RequestParam(required = false, name = "order") Orden order 
			){
		return ResponseEntity.ok(peliculaService.findAll(name,genreId,order));
	}
	
	// delete request
	@DeleteMapping("/{movieId}/characters/{characterId}")
	public ResponseEntity<String> desasociarPersonajeAPelicula(
			@PathVariable(name = "movieId") Long movieId, 
			@PathVariable(name = "characterId") Long characterId
			){
		peliculaService.desasociarPersonajeAPelicula(movieId, characterId);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{movieId}")
	public ResponseEntity<String> deleteById(
			@PathVariable(name = "movieId")Long movieId
			){
		peliculaService.deleteById(movieId);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteAll(){
		peliculaService.deleteAll();
		return ResponseEntity.noContent().build();
	}
}
