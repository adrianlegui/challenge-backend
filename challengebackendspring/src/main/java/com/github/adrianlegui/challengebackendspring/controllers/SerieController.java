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

import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.Orden;
import com.github.adrianlegui.challengebackendspring.services.SerieService;

@RestController
@RequestMapping("/series")
public class SerieController {
	@Autowired
	private SerieService serieService;

	// post request
	@PostMapping
	public ResponseEntity<SerieDTO> create(
		@RequestBody SerieDTOPOST serieDTOPOST) {

		SerieDTO serieDTO = serieService.create(serieDTOPOST);

		return new ResponseEntity<>(serieDTO, HttpStatus.CREATED);
	}

	@PostMapping("/{serieId}/characters/{characterId}")
	public ResponseEntity<SerieDTO> asociarPersonajeASerie(
		@PathVariable(name = "serieId") Long serieId,
		@PathVariable(name = "characterId") Long characterId) {

		SerieDTO serieDTO = serieService
			.asociarPersonajeASerie(serieId, characterId);

		return new ResponseEntity<>(serieDTO, HttpStatus.CREATED);
	}

	// patch request
	@PatchMapping
	public ResponseEntity<SerieDTO> update(
		@RequestBody SerieDTOPATCH serieDTOPATCH) {

		SerieDTO serieDTO = serieService.update(serieDTOPATCH);

		return ResponseEntity.ok(serieDTO);
	}

	// get request
	@GetMapping("/{serieId}")
	public ResponseEntity<SerieDTOGET> findBydId(
		@PathVariable(name = "serieId") Long serieId) {

		return ResponseEntity.ok(serieService.findById(serieId));
	}

	@GetMapping
	public ResponseEntity<List<SerieDTOGET>> findAll(
		@RequestParam(required = false, name = "name") String name,
		@RequestParam(required = false, name = "genre") Long genreId,
		@RequestParam(required = false, name = "order") Orden order) {

		return ResponseEntity
			.ok(serieService.findAll(name, genreId, order));
	}

	// delete request
	@DeleteMapping("/{serieId}/characters/{characterId}")
	public ResponseEntity<String> desasociarPersonajeDeSerie(
		@PathVariable(name = "serieId") Long serieId,
		@PathVariable(name = "characterId") Long characterId) {

		serieService.desasociarPersonajeASerie(serieId, characterId);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{serieId}")
	public ResponseEntity<String> deleteById(
		@PathVariable(name = "serieId") Long serieId) {

		serieService.deleteById(serieId);
		
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteAll(){
		serieService.deleteAll();
		
		return ResponseEntity.noContent().build();
	}

}
