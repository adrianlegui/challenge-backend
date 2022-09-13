package com.github.adrianlegui.challengebackendspring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTO;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPOST;
import com.github.adrianlegui.challengebackendspring.services.GeneroService;

@RestController
@RequestMapping("/genres")
public class GeneroController {
	@Autowired
	private GeneroService generoService;

	@PostMapping
	public ResponseEntity<GeneroDTO> create(
		@RequestBody GeneroDTOPOST generoDTOPOST) {
		GeneroDTO generoDTO = generoService.create(generoDTOPOST);
		return new ResponseEntity<>(generoDTO, HttpStatus.CREATED);
	}

}
